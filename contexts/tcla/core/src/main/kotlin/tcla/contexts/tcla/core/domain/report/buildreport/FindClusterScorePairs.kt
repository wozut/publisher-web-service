package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Clusters
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

fun List<Pair<TclDriver, TclDriverScore>>.findClusterScorePairs(): List<Pair<TclDriver, TclDriverScore>> =
    this.filter { pair -> pair.first.name.value in Clusters.all.map { it.name } }
