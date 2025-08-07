package tcla.contexts.tcla.core.domain.report.buildreport

object NonRelevantDriverNamesForTests {
    val teamCharacteristics: Set<String> = setOf(
        "Team Composition",
        "Member Roles",
        "Culture",
        "Communication",
        "Knowledge Exchange",
        "Authenticity",
        "Speaking-Up",
        "Embracing Failure"
    )
    val taskCharacteristics: Set<String> = setOf(
        "Problem Statement",
        "Complexity"
    )
    val workProcesses: Set<String> = setOf(
        "Efficiency & Effectiveness",
        "Adaptability"
    )
    val workEnvironment: Set<String> = setOf(
        "Tools"
    )

    val all: Set<String> = teamCharacteristics.plus(taskCharacteristics).plus(workProcesses).plus(workEnvironment)
}
