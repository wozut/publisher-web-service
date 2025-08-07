package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.RelevantDriverNamesByCluster
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

fun List<Pair<TclDriver, TclDriverScore>>.findRelevantTclDriverScorePairsByClusterName(cluster: Cluster): List<Pair<TclDriver, TclDriverScore>> =
    when(cluster) {
        Cluster.TeamCharacteristics ->  this.filter { RelevantDriverNamesByCluster.teamCharacteristics.contains(it.first.name.value) }
        Cluster.TaskCharacteristics -> this.filter { RelevantDriverNamesByCluster.taskCharacteristics.contains(it.first.name.value) }
        Cluster.WorkPracticesAndProcesses -> this.filter { RelevantDriverNamesByCluster.workPracticesAndProcesses.contains(it.first.name.value) }
        Cluster.WorkEnvironmentAndTools -> this.filter { RelevantDriverNamesByCluster.workEnvironmentAndTools.contains(it.first.name.value) }
    }
