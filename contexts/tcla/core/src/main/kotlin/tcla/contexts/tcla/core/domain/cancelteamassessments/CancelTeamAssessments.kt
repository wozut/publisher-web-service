package tcla.contexts.tcla.core.domain.cancelteamassessments

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flattenOrAccumulate
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.cancel.CancelAssessment
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class CancelTeamAssessments(
    private val transactionExecutor: TransactionExecutor,
    private val assessmentRepository: AssessmentRepository,
    private val cancelAssessment: CancelAssessment
) {
    fun execute(teamId: TeamId): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            assessmentRepository.searchByTeam_IdAndCurrentStepIsIn(teamId, Step.stepsFromWhichCanCancel())
                .flatMap { assessments ->
                    assessments
                        .map { assessment -> cancelAssessment.execute(assessment) }
                        .flattenOrAccumulate()
                }.flatMap { Unit.right() }
        }
}