package tcla.contexts.tcla.core.domain.reanalysetcl

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flattenOrAccumulate
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.RequesterIsSuperAdminRule
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.ReanalyseTclFailure
import tcla.contexts.tcla.core.domain.analysetcl.AnalyseTcl
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreRepository
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid


@Named
class ReanalyseTcl(
    private val tclDriverScoreRepository: TclDriverScoreRepository,
    private val transactionExecutor: TransactionExecutor,
    private val analyseTcl: AnalyseTcl,
    private val requesterIsSuperAdminRule: RequesterIsSuperAdminRule,
    private val assessmentRepository: AssessmentRepository
) {

    fun execute(assessmentUuids: List<String>): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            requesterIsSuperAdminRule.ensure()
                .flatMap { ensureAssessmentIdsNotRepeated(assessmentUuids) }
                .flatMap { toAssessmentIds(assessmentUuids) }
                .flatMap { assessmentIds -> assessmentRepository.findAllById(assessmentIds) }
                .flatMap { assessments -> ensureAllAssessmentsExists(assessments, assessmentUuids) }
                .flatMap { assessments ->
                    assessments.map { assessment: Assessment ->
                        assessment.ensureIsInAppropriateStep()
                            .flatMap { tclDriverScoreRepository.deleteByAssessmentId(assessment.id) }
                            .flatMap { assessment.stepBackwardTo(Step.AnalysableData) }
                            .flatMap { updatedAssessment -> assessmentRepository.save(updatedAssessment) }
                            .flatMap { updatedAssessment -> analyseTcl.execute(updatedAssessment.id) }
                    }.flattenOrAccumulate()
                }
                .flatMap { Unit.right() }
        }

    private fun ensureAllAssessmentsExists(
        assessments: List<Assessment>,
        assessmentUuids: List<String>
    ): Either<NonEmptyList<Failure>, List<Assessment>> = when (assessments.size == assessmentUuids.size) {
        true -> assessments.right()
        false -> Failure.EntityNotFound.Assessment.nel().left()
    }

    private fun toAssessmentIds(assessmentUuids: List<String>): Either<NonEmptyList<Failure>, List<AssessmentId>> =
        assessmentUuids
            .map { uuidAsString ->
                uuidAsString
                    .toUuid()
                    .mapLeft { Failure.StringIsNotUuid.AssessmentId }
                    .flatMap { uuid -> AssessmentId(uuid).right() }
            }.flattenOrAccumulate()

    private fun ensureAssessmentIdsNotRepeated(assessmentUuids: List<String>): Either<NonEmptyList<Failure>, Unit> =
        when (assessmentUuids.size == assessmentUuids.distinct().size) {
            true -> Unit.right()
            false -> ReanalyseTclFailure.DuplicatedAssessmentIds.nel().left()
        }

    private fun Assessment.ensureIsInAppropriateStep(): Either<NonEmptyList<Failure>, Assessment> =
        either {
            ensure(currentStep == Step.ResultsAvailable) { nonEmptyListOf(ReanalyseTclFailure.AssessmentHasNotAnAppropriateStatus) }
            this@ensureIsInAppropriateStep
        }
}


