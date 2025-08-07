package tcla.contexts.tcla.core.domain.questionnaire.rule

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.NotOverlappingQuestionnairesByTeamRuleFailure
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import tcla.contexts.tcla.core.domain.team.model.TeamId

@Named
class NotOverlappingQuestionnairesByTeamRule(
    private val questionnaireRepository: QuestionnaireRepository,
) {

    fun ensure(
        questionnaireId: QuestionnaireId,
        newQuestionnaireInterval: ResponseAcceptanceInterval,
        teamId: TeamId
    ): Either<NonEmptyList<Failure>, Unit> =
        questionnaireRepository.searchByAssessment_Team_IdAndAssessment_CurrentStepIsIn(teamId, setOf(Step.Scheduled, Step.CollectingData))
            .flatMap { existingQuestionnaires: List<Questionnaire> ->
                val anyOverlaps =
                    existingQuestionnaires
                        .filterNot { questionnaire -> questionnaire.id == questionnaireId }
                        .any { questionnaire -> questionnaire.responseAcceptanceInterval.overlaps(newQuestionnaireInterval) }
                when (anyOverlaps) {
                    true -> NotOverlappingQuestionnairesByTeamRuleFailure.OverlapsWithAnotherAssessment.nel().left()
                    false -> Unit.right()
                }
            }

}
