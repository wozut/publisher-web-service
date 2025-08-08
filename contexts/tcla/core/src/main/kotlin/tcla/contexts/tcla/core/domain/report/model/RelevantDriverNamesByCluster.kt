package tcla.contexts.tcla.core.domain.report.model

object RelevantDriverNamesByCluster {
    val teamCharacteristics: Set<String> = setOf(
        DriverTexts.teamComplexity.capitalized,
        DriverTexts.teamCompetence.capitalized,
        DriverTexts.roleClarity.capitalized,
        DriverTexts.roleFit.capitalized,
        DriverTexts.roleLoad.capitalized,
        DriverTexts.teamInteraction.capitalized,
        DriverTexts.teamAlignment.capitalized,
        DriverTexts.memberPsychologicalSafety.capitalized
    )
    val taskCharacteristics: Set<String> = setOf(
        DriverTexts.problemDefinition.capitalized,
        DriverTexts.solutionAlignment.capitalized,
        DriverTexts.taskComplexity.capitalized,
        DriverTexts.contextualComplexity.capitalized,
        DriverTexts.metrics.capitalized
    )
    val workPracticesAndProcesses: Set<String> = setOf(
        DriverTexts.useOfInformation.capitalized,
        DriverTexts.process.capitalized,
        DriverTexts.consistency.capitalized,
        DriverTexts.pace.capitalized,
        DriverTexts.performance.capitalized,
        DriverTexts.resilience.capitalized,
        DriverTexts.iterativeWorking.capitalized,
        DriverTexts.continuousLearning.capitalized
    )
    val workEnvironmentAndTools: Set<String> = setOf(
        DriverTexts.toolSuitability.capitalized,
        DriverTexts.toolPerformance.capitalized,
        DriverTexts.environment.capitalized
    )

    val all: Set<String> = teamCharacteristics.plus(taskCharacteristics).plus(workPracticesAndProcesses).plus(workEnvironmentAndTools)
}
