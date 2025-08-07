package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

fun List<Pair<TclDriver, TclDriverScore>>.findClusterScorePairsWithHighestScore(
    range: Double
): List<TclDriverAndScore> {
    val allSorted = findClusterScorePairsSortedByScore()
    val highest = allSorted.first()
    return allSorted.findTclDriverScorePairsWithHighestScore(highestScore = highest.second.value.value, range = range)
}
