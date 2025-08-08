package tcla.contexts.tcla.core.domain.report.model

fun Cluster.toText(): String {
    val textForms = when (this) {
        Cluster.TeamCharacteristics -> ClusterTexts.teamCharacteristics
        Cluster.TaskCharacteristics -> ClusterTexts.taskCharacteristics
        Cluster.WorkPracticesAndProcesses -> ClusterTexts.workPracticesProcesses
        Cluster.WorkEnvironmentAndTools -> ClusterTexts.workEnvironmentTools
    }

    return when(this) {
        Cluster.TeamCharacteristics ->
            "<cluster entry-key=\"$id\" cluster-id=\"$id\">${textForms.capitalized}</cluster>"
        Cluster.TaskCharacteristics -> TODO()
        Cluster.WorkPracticesAndProcesses -> TODO()
        Cluster.WorkEnvironmentAndTools -> TODO()
    }

}
