package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.RelevantDriverNamesByCluster
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

fun List<Pair<TclDriver, TclDriverScore>>.findRelevantDriverScorePairs(): List<Pair<TclDriver, TclDriverScore>> =
    this.filter { it.first.name.value in RelevantDriverNamesByCluster.all }
