package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.toEnumeration

fun Iterable<Cluster>.markUpClusterEnumeration(): String = map(Cluster::markUp).toEnumeration()
