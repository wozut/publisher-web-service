package tcla.contexts.tcla.core.domain.report.model

import java.time.Instant

object ReportMother {
    fun default(
        id: ReportId = ReportIdMother.default(),
        sections: Sections = SectionsMother.default(),
        definitions: Dictionary = Dictionary,
        copyrightText: CopyrightText = CopyrightTextMother.default(),
        shareableToken: ShareableToken = ShareableToken("123"),
        responsesStats: ResponsesStats = ResponsesStats(
            maximumAmountToBeCollected = 1,
            minimumRateRequired = 1.0,
            minimumAmountRequired = 1,
            currentRateCollected = 0.0,
            currentAmountCollected = 0
        ),
        period: Period = Period(Instant.parse("2023-12-12T19:46:15.184489461Z"), Instant.parse("2023-12-14T19:46:15.184497860Z")),
        organizationName: String = "ACME",
        teamName: String = "Avengers",
        assessmentTitle: String = "Spring"
    ): Report = Report(
        id = id,
        sections = sections,
        dictionary = definitions,
        copyrightText = copyrightText,
        shareableToken = shareableToken,
        responsesStats = responsesStats,
        period = period,
        organizationName = organizationName,
        teamName = teamName,
        assessmentTitle = assessmentTitle
    )
}
