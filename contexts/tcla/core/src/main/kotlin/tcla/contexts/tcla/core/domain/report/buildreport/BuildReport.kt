package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.ResultsShareableToken
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import tcla.contexts.tcla.core.domain.questionnaire.model.Responses
import tcla.contexts.tcla.core.domain.report.model.BodyPart
import tcla.contexts.tcla.core.domain.report.model.CopyrightText
import tcla.contexts.tcla.core.domain.report.model.Dictionary
import tcla.contexts.tcla.core.domain.report.model.Name
import tcla.contexts.tcla.core.domain.report.model.Period
import tcla.contexts.tcla.core.domain.report.model.Report
import tcla.contexts.tcla.core.domain.report.model.ReportId
import tcla.contexts.tcla.core.domain.report.model.ResponsesStats
import tcla.contexts.tcla.core.domain.report.model.Score
import tcla.contexts.tcla.core.domain.report.model.Section
import tcla.contexts.tcla.core.domain.report.model.Sections
import tcla.contexts.tcla.core.domain.report.model.ShareableToken
import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore
import tcla.contexts.tcla.core.domain.report.model.Title
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import java.util.UUID

@Named
class BuildReport(
    private val reportTextBuilder: ReportTextBuilder,
    private val faqSectionBuilder: FAQSectionBuilder,
    private val introductionSectionBuilder: IntroductionSectionBuilder,
    private val buildQuickSummary: BuildQuickSummary,
    private val buildAnalysisSections: BuildAnalysisSections,
    private val assessmentRepository: AssessmentRepository
) {
    private companion object {
        const val SCORE_RANGE_FROM_HIGHEST: Double = 0.3
    }

    fun execute(
        tclDriverScorePairs: List<Pair<TclDriver, TclDriverScore>>,
        resultsShareableToken: ResultsShareableToken,
        responses: Responses,
        responseAcceptanceInterval: ResponseAcceptanceInterval,
        organizationName: String?,
        teamName: String,
        assessmentTitle: String
    ): Report {
        return Report(
            id = ReportId(UUID.randomUUID()),
            copyrightText = CopyrightText("Copyright (c) 2023 Team Topologies Ltd and FlowOnRails SL. All Rights Reserved."),
            sections = Sections(
                quickSummary = buildQuickSummary.execute(tclDriverScorePairs, SCORE_RANGE_FROM_HIGHEST),
                introduction = Section(
                    title = Title(introductionSectionBuilder.title()),
                    body = listOf(
                        BodyPart(
                            text = BodyPart.Text(introductionSectionBuilder.buildIntroductionBody()),
                            order = BodyPart.Order(1)
                        )
                    )
                ),
                overview = Section(
                    title = Title("# Where do the highest drivers come from"),
                    body = listOf(
                        BodyPart(
                            text = BodyPart.Text(reportTextBuilder.buildWhereHighestDriversComeFromText(
                                tclDriverScorePairs,
                                SCORE_RANGE_FROM_HIGHEST
                            )),
                            order = BodyPart.Order(1)
                        )
                    ),
                    tclDriverAndScoreList = tclDriverScorePairs
                        .filter { it.first.isCluster }
                        .map {
                            TclDriverAndScore(
                                id = it.first.name.value.toCluster()?.id,
                                name = Name(it.first.name.value),
                                score = Score(it.second.value.value)
                            )
                        }
                ),
                analysis = buildAnalysisSections.execute(tclDriverScorePairs, SCORE_RANGE_FROM_HIGHEST),
                annex = listOf(
                    buildFAQSection(FAQSections.ABOUT_TCLA, 1),
                    buildFAQSection(FAQSections.TCLA_METHODOLOGY, 2),
                    buildFAQSection(FAQSections.TASK_CHARACTERISTICS_ABOUT, 3),
                    buildFAQSection(FAQSections.TEAM_CHARACTERISTICS_ABOUT, 4),
                    buildFAQSection(FAQSections.WORK_PROCESSES_PRACTICES_ABOUT, 5),
                    buildFAQSection(FAQSections.WORK_ENVIRONMENT_TOOLS_ABOUT, 6),
                )
            ),
            dictionary = Dictionary,
            shareableToken = ShareableToken(resultsShareableToken.token),
            responsesStats = ResponsesStats(
                maximumAmountToBeCollected = responses.maximumAmountToBeCollected.int,
                minimumRateRequired = responses.minimumRateRequired.double,
                minimumAmountRequired = responses.minimumAmountRequired,
                currentRateCollected = responses.currentRateCollected,
                currentAmountCollected = responses.currentAmountCollected
            ),
            period = Period(responseAcceptanceInterval.start, responseAcceptanceInterval.end),
            organizationName = organizationName,
            teamName = teamName,
            assessmentTitle = assessmentTitle
        )
    }

    private fun buildFAQSection(sectionName: FAQSections, order: Int): Section =
        Section(
            title = Title(faqSectionBuilder.titleFor(sectionName)),
            body = listOf(
                BodyPart(
                    text = BodyPart.Text(faqSectionBuilder.faqText(sectionName)),
                    order = BodyPart.Order(order),
                )
            )
        )
}
