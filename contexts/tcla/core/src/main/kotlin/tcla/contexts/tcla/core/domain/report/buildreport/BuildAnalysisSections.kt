package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Section
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

@Named
class BuildAnalysisSections(
    private val buildAnalysisSection: BuildAnalysisSection
) {
    fun execute(tclDriverScorePairs: List<Pair<TclDriver, TclDriverScore>>, range: Double): List<Section> =
        tclDriverScorePairs
            .findClusterScorePairsSortedByScore()
            .mapNotNull { it.first.name.value.toCluster() }
            .map { cluster: Cluster ->
                buildAnalysisSection.execute(
                    cluster = cluster,
                    tclDriverScorePairs = tclDriverScorePairs.findRelevantTclDriverScorePairsByClusterName(cluster),
                    range = range
                )
            }
}
