package tcla.contexts.tcla.core.domain.report.model

sealed class Cluster(val id: String, val name: String) {
    data object TeamCharacteristics: Cluster(id = "team-characteristics", name = ClusterTexts.teamCharacteristics.capitalized)
    data object TaskCharacteristics: Cluster(id = "task-characteristics", name = ClusterTexts.taskCharacteristics.capitalized)
    data object WorkPracticesAndProcesses: Cluster(id = "work-practices-and-processes", name = ClusterTexts.workPracticesProcesses.capitalized)
    data object WorkEnvironmentAndTools: Cluster(id = "work-environment-and-tools", name = ClusterTexts.workEnvironmentTools.capitalized)
}
