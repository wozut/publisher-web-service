package tcla.contexts.tcla.webapi.springweb.questionnaire.jsonapi

import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval


data class QuestionnaireResource(
    val id: String,
    val attributes: QuestionnaireResourceAttributes,
) {
    val type: String = QUESTIONNAIRE_JSON_API_TYPE
}

fun Questionnaire.toResource(): QuestionnaireResource =
    QuestionnaireResource(
        id = id.uuid.toString(),
        attributes = QuestionnaireResourceAttributes(
            externalQuestionnaireId = when (externalQuestionnaireId) {
                null -> null
                else -> externalQuestionnaireId!!.value
            },
            responseAcceptanceInterval = responseAcceptanceInterval.toResource(),
            assessmentId = assessmentId.uuid.toString(),
            maximumAmountToBeCollected = responses.maximumAmountToBeCollected.int,
            minimumRateRequired = responses.minimumRateRequired.double,
            minimumAmountRequired = responses.minimumAmountRequired,
            currentRateCollected = responses.currentRateCollected,
            currentAmountCollected = responses.currentAmountCollected
        )
    )

private fun ResponseAcceptanceInterval.toResource(): ResponseAcceptanceIntervalResource =
    ResponseAcceptanceIntervalResource(
        start = start.toString(),
        end = end.toString()
    )
