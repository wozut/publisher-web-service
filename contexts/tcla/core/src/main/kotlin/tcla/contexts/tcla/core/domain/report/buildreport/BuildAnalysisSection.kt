package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.report.model.BodyPart
import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Name
import tcla.contexts.tcla.core.domain.report.model.Score
import tcla.contexts.tcla.core.domain.report.model.Section
import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore
import tcla.contexts.tcla.core.domain.report.model.Title
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

@Named
class BuildAnalysisSection(
    private val clusterSectionBuilder: ClusterSectionBuilder
) {

    fun execute(
        cluster: Cluster,
        tclDriverScorePairs: List<Pair<TclDriver, TclDriverScore>>,
        range: Double
    ): Section {
        val bodyParts = mutableListOf(
            BodyPart(
                text = BodyPart.Text(clusterSectionBuilder.buildIntroductionTitle()),
                order = BodyPart.Order(1)
            ),
            BodyPart(
                text = BodyPart.Text(clusterSectionBuilder.buildIntroductionBody(cluster)),
                order = BodyPart.Order(2)
            )
        )

        val questionsAndRecommendedReadings: String? = clusterSectionBuilder
            .buildQuestionsAndRecommendedReadingsBody(
                tclDriverScorePairs.findRelevantDriverScorePairsWithHighestScore(range)
            )

        if(questionsAndRecommendedReadings != null) {
            val questionsAndRecommendedReadingsBodyPart = BodyPart(
                text = BodyPart.Text(questionsAndRecommendedReadings),
                order = BodyPart.Order(3)
            )
            bodyParts.add(questionsAndRecommendedReadingsBodyPart)
        }

        return Section(
            title = Title("# Details for ${cluster.name} cluster"),
            body = bodyParts,
            tclDriverAndScoreList = tclDriverScorePairs
                .map {
                    TclDriverAndScore(
                        id = it.first.name.value.toRelevantDriver()?.id,
                        name = Name(it.first.name.value),
                        score = Score(it.second.value.value)
                    )
                }
        )
    }
}
