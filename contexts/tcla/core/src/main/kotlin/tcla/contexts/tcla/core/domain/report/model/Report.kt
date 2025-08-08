package tcla.contexts.tcla.core.domain.report.model

data class Report(
    val id: ReportId,
    val sections: Sections,
    val dictionary: Dictionary,
    val copyrightText: CopyrightText,
    val shareableToken: ShareableToken,
    val responsesStats: ResponsesStats,
    val period: Period,
    val organizationName: String?,
    val teamName: String,
    val assessmentTitle: String
)
