package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Name
import tcla.contexts.tcla.core.domain.report.model.Score
import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

fun List<Pair<TclDriver, TclDriverScore>>.findTclDriverScorePairsWithHighestScore(
    highestScore: Double,
    range: Double
): List<TclDriverAndScore> = this
    .filter { it.second.value.value in (highestScore - range)..highestScore }
    .map {
        TclDriverAndScore(
            id = it.first.name.value.toClusterOrRelevantDriverId(),
            name = Name(it.first.name.value),
            score = Score(it.second.value.value)
        )
    }

private fun String.toClusterOrRelevantDriverId(): String? =
    when (val cluster: Cluster? = this.toCluster()) {
        null -> this.toRelevantDriver()?.id
        else -> cluster.id
    }
