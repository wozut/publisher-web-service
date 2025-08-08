package tcla.contexts.tcla.core.domain.report.buildreport

object RelevantDriverNamesForTests {

    const val TEAM_COMPLEXITY = "Team Complexity"
    const val PROBLEM_DEFINITION = "Problem Definition"
    const val SOLUTION_ALIGNMENT = "Solution Alignment"
    const val USE_OF_INFORMATION = "Use Of Information"
    const val TOOL_SUITABILITY = "Tool Suitability"

    val teamCharacteristics: Set<String> = setOf(
        TEAM_COMPLEXITY,
        "Team Competence",
        "Role Clarity",
        "Role Fit",
        "Role Load",
        "Team Interaction",
        "Team Alignment",
        "Member Psychological Safety",
    )
    val taskCharacteristics: Set<String> = setOf(
        PROBLEM_DEFINITION,
        SOLUTION_ALIGNMENT,
        "Task Complexity",
        "Contextual Complexity",
        "Metrics"
    )
    val workProcesses: Set<String> = setOf(
        USE_OF_INFORMATION,
        "Process",
        "Consistency",
        "Pace",
        "Performance",
        "Resilience",
        "Iterative Working",
        "Continuous Learning",
    )
    val workEnvironment: Set<String> = setOf(
        TOOL_SUITABILITY,
        "Tool Performance",
        "Environment"
    )

    val all: Set<String> = teamCharacteristics.plus(taskCharacteristics).plus(workProcesses).plus(workEnvironment)
}
