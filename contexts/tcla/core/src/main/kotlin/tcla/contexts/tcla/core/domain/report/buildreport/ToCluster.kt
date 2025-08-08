package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Clusters

fun String.toCluster(): Cluster? = Clusters.all.find { cluster -> cluster.name == this }
