package tcla.contexts.tcla.webapi.springweb.report.jsonapi

import tcla.contexts.tcla.core.domain.report.model.Report


data class ReportResource(
    val id: String,
    val attributes: ReportResourceAttributes,
) {
    val type: String = "report"
}

fun Report.toResource(): ReportResource =
    ReportResource(
        id = id.value.toString(),
        attributes = ReportResourceAttributes(
            sections = sections.toResource(),
            dictionary = dictionary.toResource(),
            copyrightText = copyrightText.value,
            shareableToken = shareableToken.token,
            responsesStats = ResponsesStatsResource(
                maximumAmountToBeCollected = responsesStats.maximumAmountToBeCollected,
                minimumRateRequired = responsesStats.minimumRateRequired,
                minimumAmountRequired = responsesStats.minimumAmountRequired,
                currentRateCollected = responsesStats.currentRateCollected,
                currentAmountCollected = responsesStats.currentAmountCollected
            ),
            period = PeriodResource(
                start = period.start.toString(),
                end = period.end.toString()
            ),
            organizationName = organizationName,
            teamName = teamName,
            assessmentTitle = assessmentTitle
        )
    )
