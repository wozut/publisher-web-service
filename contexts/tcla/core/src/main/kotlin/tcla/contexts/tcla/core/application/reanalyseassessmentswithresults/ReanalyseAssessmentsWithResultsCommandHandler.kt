package tcla.contexts.tcla.core.application.reanalyseassessmentswithresults

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.RequesterIsSuperAdminRule
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.reanalysetcl.ReanalyseTcl
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor


@Named
class ReanalyseAssessmentsWithResultsCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val assessmentRepository: AssessmentRepository,
    private val reanalyseTcl: ReanalyseTcl,
    private val requesterIsSuperAdminRule: RequesterIsSuperAdminRule
) {

    fun execute(command: ReanalyseAssessmentsWithResultsCommand): Either<NonEmptyList<Failure>, ReanalyseAssessmentsWithResultsSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            requesterIsSuperAdminRule.ensure()
                .flatMap { assessmentRepository.searchByCurrentStep(Step.ResultsAvailable) }
                .flatMap { assessments: List<Assessment> -> assessments.map { it.id.uuid.toString() }.right() }
                .flatMap { assessmentUuidAsStrings: List<String> -> reanalyseTcl.execute(assessmentUuidAsStrings) }
                .flatMap { ReanalyseAssessmentsWithResultsSuccess.right() }
        }
}
