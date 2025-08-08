package tcla.contexts.tcla.webapi.springweb.report.jsonapi

data class ResponsesStatsResource(
    val maximumAmountToBeCollected: Int,
    val minimumRateRequired: Double,
    val minimumAmountRequired: Int,
    val currentRateCollected: Double,
    val currentAmountCollected: Int
)
