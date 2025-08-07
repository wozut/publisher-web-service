package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

fun List<Pair<TclDriver, TclDriverScore>>.findClusterScorePairsSortedByScore(): List<Pair<TclDriver, TclDriverScore>> =
    this.findClusterScorePairs().sortedByDescending { pair -> pair.second }
