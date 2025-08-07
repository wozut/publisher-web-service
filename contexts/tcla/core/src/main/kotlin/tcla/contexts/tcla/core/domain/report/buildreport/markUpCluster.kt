package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Cluster

fun Cluster.markUp(): String = "<cluster entry-key=\"${id}\" cluster-id=\"${id}\">${name}</cluster>"
