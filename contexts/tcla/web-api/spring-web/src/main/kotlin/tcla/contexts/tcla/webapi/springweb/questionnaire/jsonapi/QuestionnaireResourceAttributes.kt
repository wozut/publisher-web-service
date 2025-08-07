package tcla.contexts.tcla.webapi.springweb.questionnaire.jsonapi


data class QuestionnaireResourceAttributes(
    val externalQuestionnaireId: String?,
    val responseAcceptanceInterval: ResponseAcceptanceIntervalResource,
    val maximumAmountToBeCollected: Int,
    val minimumRateRequired: Double,
    val minimumAmountRequired: Int,
    val currentRateCollected: Double,
    val currentAmountCollected: Int,
    val assessmentId: String
)
