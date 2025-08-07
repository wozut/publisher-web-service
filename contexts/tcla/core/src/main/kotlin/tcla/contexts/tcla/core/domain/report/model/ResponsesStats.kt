package tcla.contexts.tcla.core.domain.report.model

data class ResponsesStats (
    val maximumAmountToBeCollected: Int,
    val minimumRateRequired: Double,
    val minimumAmountRequired: Int,
    val currentRateCollected: Double,
    val currentAmountCollected: Int
)
