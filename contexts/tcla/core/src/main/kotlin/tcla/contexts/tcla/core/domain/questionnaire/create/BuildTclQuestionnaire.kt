package tcla.contexts.tcla.core.domain.questionnaire.create

import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questionnaire.model.*


fun buildTclQuestionnaire(
    questionnaireId: QuestionnaireId,
    externalQuestionnaireId: ExternalQuestionnaireId? = null,
    externalQuestionnaireIsPublic: Boolean = false,
    assessmentId: AssessmentId,
    interval: ResponseAcceptanceInterval,
    maximumAmountToBeCollected: Int,
    includeGenderQuestion: Boolean,
    includeWorkFamiliarityQuestion: Boolean,
    includeTeamFamiliarityQuestion: Boolean,
    includeModeOfWorkingQuestion: Boolean
): Questionnaire = Questionnaire(
    id = questionnaireId,
    externalQuestionnaireId = externalQuestionnaireId,
    responseAcceptanceInterval = interval,
    externalQuestionnaireIsPublic = externalQuestionnaireIsPublic,
    questions = buildQuestions(
        includeGenderQuestion = includeGenderQuestion,
        includeWorkFamiliarityQuestion = includeWorkFamiliarityQuestion,
        includeTeamFamiliarityQuestion = includeTeamFamiliarityQuestion,
        includeModeOfWorkingQuestion = includeModeOfWorkingQuestion
    ),
    assessmentId = assessmentId,
    responses = Responses(
        collection = setOf(),
        maximumAmountToBeCollected = Responses.MaximumAmountToBeCollected(maximumAmountToBeCollected),
        minimumRateRequired = Responses.MinimumRateRequired(minimumRequired(maximumAmountToBeCollected))
    )
)

private fun minimumRequired(teamSize: Int): Double = when (teamSize) {
    in 1..5 -> 1.0
    else -> 0.8
}

