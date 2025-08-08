package tcla.contexts.tcla.core.domain.report.buildreport

object ClusterNamesForTests {
    const val TEAM_CHARACTERISTICS_CLUSTER_NAME = "Team Characteristics"
    const val TASK_CHARACTERISTICS_CLUSTER_NAME = "Task Characteristics"
    const val WORK_PRACTICES_PROCESSES_CLUSTER_NAME = "Work Practices & Processes"
    const val WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME = "Work Environment & Tools"

    val all = setOf(
        TEAM_CHARACTERISTICS_CLUSTER_NAME,
        TASK_CHARACTERISTICS_CLUSTER_NAME,
        WORK_PRACTICES_PROCESSES_CLUSTER_NAME,
        WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME
    )
}
