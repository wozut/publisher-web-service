package tcla.contexts.tcla.webapi.springweb.report.jsonapi


data class ReportResourceAttributes(
    val sections: SectionsResource,
    val dictionary: List<EntryResource>,
    val copyrightText: String,
    val shareableToken: String,
    val responsesStats: ResponsesStatsResource,
    val period: PeriodResource,
    val organizationName: String?,
    val teamName: String,
    val assessmentTitle: String
)
