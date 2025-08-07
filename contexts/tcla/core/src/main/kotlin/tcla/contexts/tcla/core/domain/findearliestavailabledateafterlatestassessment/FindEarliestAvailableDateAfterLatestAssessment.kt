package tcla.contexts.tcla.core.domain.findearliestavailabledateafterlatestassessment

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.time.Instant

@Named
class FindEarliestAvailableDateAfterLatestAssessment(
    private val transactionExecutor: TransactionExecutor,
    private val questionnaireRepository: QuestionnaireRepository
) {
    fun execute(teamId: TeamId): Either<NonEmptyList<Failure>, Instant> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            questionnaireRepository.searchFirstByAssessment_Team_IdAndAssessment_CurrentStepIsNotOrderByEndDateDesc(
                teamId = teamId,
                step = Step.Canceled
            ).flatMap { questionnaires: List<Questionnaire> ->
                when(questionnaires.isEmpty()) {
                    true -> Instant.now()
                    false -> buildDate(questionnaires.first())
                }.right()
            }
        }

    private fun buildDate(questionnaire: Questionnaire): Instant =
        questionnaire.responseAcceptanceInterval.end.plusNanos(1)
}