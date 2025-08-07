package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Term
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverName
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreId
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreMother
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreValue
import java.util.UUID

class ReportTextBuilderTest {

    @Test
    fun `Team name and TCL Levels Qualification label for cluster must be present in overview's introduction section`() {
        val reportTextBuilder = ReportTextBuilder()

        val actualOverviewText = reportTextBuilder.buildOverviewIntroductionSection(
            tclClusterScorePairsWithinReasonableRange
        )

        assertThat(actualOverviewText).isEqualTo(expectedOverviewIntroductionWithTeamName)
    }


    @Test
    fun `TCL Cluster names should be included in overview's results section`() {
        val reportTextBuilder = ReportTextBuilder()

        val actualOverviewText = reportTextBuilder.buildOverviewResultsSection(tclClusterScorePairsWithRandomNames)

        assertThat(actualOverviewText).isEqualTo(expectedOverviewResultsText)
    }


    @Test
    fun `Build tailored assessment in overview section for a single cluster that should be addressed first`() {
        val reportTextBuilder = ReportTextBuilder()

        val actualTailoredAssessmentWhenSingleAreaNeedsToBeAddressedFirst =
            reportTextBuilder.buildTailoredAssessmentInOverviewSection(
                tclClusterScorePairsWithinReasonableRange
            )

        assertThat(actualTailoredAssessmentWhenSingleAreaNeedsToBeAddressedFirst).isEqualTo(
            expectedTailoredAssessmentWhenSingleAreaNeedsToBeAddressedFirst
        )
    }

    @Test
    fun `Build tailored assessment in overview section for more than one cluster that should be addressed first`() {
        val reportTextBuilder = ReportTextBuilder()

        val actualTailoredAssessmentWhenSingleAreaNeedsToBeAddressedFirst = reportTextBuilder
            .buildTailoredAssessmentInOverviewSection(multipleTclClusterScorePairsThatNeedToBeAddressed)

        assertThat(actualTailoredAssessmentWhenSingleAreaNeedsToBeAddressedFirst)
            .isEqualTo(expectedTailoredAssessmentWhenMultipleAreasNeedToBeAddressedFirst)
    }

    @Test
    fun `text for 'where do the highest drivers come from' when 1 cluster in the list`() {
        val tclDriverScorePairs = listOf(
            buildTclDriverScorePair(ClusterNamesForTests.TASK_CHARACTERISTICS_CLUSTER_NAME, 4.7),
            buildTclDriverScorePair(ClusterNamesForTests.TEAM_CHARACTERISTICS_CLUSTER_NAME, 4.5),
            buildTclDriverScorePair(ClusterNamesForTests.WORK_PRACTICES_PROCESSES_CLUSTER_NAME, 4.3),
            buildTclDriverScorePair(ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME, 4.1),
            buildTclDriverScorePair(RelevantDriverNamesForTests.PROBLEM_DEFINITION, 4.7),
            buildTclDriverScorePair(RelevantDriverNamesForTests.SOLUTION_ALIGNMENT, 4.69),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TEAM_COMPLEXITY, 4.5),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TOOL_SUITABILITY, 4.3),
            buildTclDriverScorePair(RelevantDriverNamesForTests.USE_OF_INFORMATION, 4.1)
        )

        val expectedText =
            Term.TeamCognitiveLoadDriver.markUp("Team Cognitive Load drivers") +
                    " are organized in four different ${Term.Cluster.markUp("clusters")}: " +
                    "${Cluster.TeamCharacteristics.markUp()}, ${Cluster.TaskCharacteristics.markUp()}, ${Cluster.WorkPracticesAndProcesses.markUp()}, and ${Cluster.WorkEnvironmentAndTools.markUp()}." +
                    "\n" +
                    "\n" +
                    "The highest drivers highlighted in the Quick summary above are coming from the following cluster:\n\n" +
                    "* Task Characteristics: ${RelevantDriverNamesForTests.PROBLEM_DEFINITION} and ${RelevantDriverNamesForTests.SOLUTION_ALIGNMENT}" +
                    "\n\n" +
                    "The results per cluster, calculated as the average of all drivers per cluster, indicate that " +
                    "the highest cluster is: ${ClusterNamesForTests.TASK_CHARACTERISTICS_CLUSTER_NAME}."


        val reportTextBuilder = ReportTextBuilder()

        val actualOverviewText =
            reportTextBuilder.buildWhereHighestDriversComeFromText(tclDriverScorePairs, 0.1)

        assertThat(actualOverviewText).isEqualTo(expectedText)
    }

    @Test
    fun `text for 'where do the highest drivers come from' when 2 clusters in the list`() {
        val tclDriverScorePairs = listOf(
            buildTclDriverScorePair(ClusterNamesForTests.TASK_CHARACTERISTICS_CLUSTER_NAME, 4.7),
            buildTclDriverScorePair(ClusterNamesForTests.TEAM_CHARACTERISTICS_CLUSTER_NAME, 4.69),
            buildTclDriverScorePair(ClusterNamesForTests.WORK_PRACTICES_PROCESSES_CLUSTER_NAME, 4.3),
            buildTclDriverScorePair(ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME, 4.1),
            buildTclDriverScorePair(RelevantDriverNamesForTests.PROBLEM_DEFINITION, 4.7),
            buildTclDriverScorePair(RelevantDriverNamesForTests.SOLUTION_ALIGNMENT, 4.69),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TEAM_COMPLEXITY, 4.5),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TOOL_SUITABILITY, 4.3),
            buildTclDriverScorePair(RelevantDriverNamesForTests.USE_OF_INFORMATION, 4.1)
        )

        val expectedText =
            Term.TeamCognitiveLoadDriver.markUp("Team Cognitive Load drivers") +
                    " are organized in four different ${Term.Cluster.markUp("clusters")}: " +
                    "${Cluster.TeamCharacteristics.markUp()}, ${Cluster.TaskCharacteristics.markUp()}, ${Cluster.WorkPracticesAndProcesses.markUp()}, and ${Cluster.WorkEnvironmentAndTools.markUp()}." +
                    "\n" +
                    "\n" +
                    "The highest drivers highlighted in the Quick summary above are coming from the following clusters:\n\n" +
                    "* Task Characteristics: ${RelevantDriverNamesForTests.PROBLEM_DEFINITION} and ${RelevantDriverNamesForTests.SOLUTION_ALIGNMENT}\n" +
                    "* Team Characteristics: ${RelevantDriverNamesForTests.TEAM_COMPLEXITY}" +
                    "\n\n" +
                    "The results per cluster, calculated as the average of all drivers per cluster, indicate that " +
                    "the highest clusters are: ${ClusterNamesForTests.TASK_CHARACTERISTICS_CLUSTER_NAME} and ${ClusterNamesForTests.TEAM_CHARACTERISTICS_CLUSTER_NAME}."

        val reportTextBuilder = ReportTextBuilder()

        val actualOverviewText =
            reportTextBuilder.buildWhereHighestDriversComeFromText(tclDriverScorePairs, 0.3)

        assertThat(actualOverviewText).isEqualTo(expectedText)
    }

    @Test
    fun `text for 'where do the highest drivers come from' with extra paragraph with 1 cluster in the list`() {
        val tclDriverScorePairs = listOf(
            buildTclDriverScorePair(ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME, 4.7),
            buildTclDriverScorePair(ClusterNamesForTests.TASK_CHARACTERISTICS_CLUSTER_NAME, 4.1),
            buildTclDriverScorePair(ClusterNamesForTests.TEAM_CHARACTERISTICS_CLUSTER_NAME, 4.5),
            buildTclDriverScorePair(ClusterNamesForTests.WORK_PRACTICES_PROCESSES_CLUSTER_NAME, 4.3),
            buildTclDriverScorePair(RelevantDriverNamesForTests.PROBLEM_DEFINITION, 4.7),
            buildTclDriverScorePair(RelevantDriverNamesForTests.SOLUTION_ALIGNMENT, 4.69),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TEAM_COMPLEXITY, 4.5),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TOOL_SUITABILITY, 4.3),
            buildTclDriverScorePair(RelevantDriverNamesForTests.USE_OF_INFORMATION, 4.1)
        )

        val expectedText =
            Term.TeamCognitiveLoadDriver.markUp("Team Cognitive Load drivers") +
                    " are organized in four different ${Term.Cluster.markUp("clusters")}: " +
                    "${Cluster.TeamCharacteristics.markUp()}, ${Cluster.TaskCharacteristics.markUp()}, ${Cluster.WorkPracticesAndProcesses.markUp()}, and ${Cluster.WorkEnvironmentAndTools.markUp()}." +
                    "\n" +
                    "\n" +
                    "The highest drivers highlighted in the Quick summary above are coming from the following cluster:\n\n" +
                    "* Task Characteristics: ${RelevantDriverNamesForTests.PROBLEM_DEFINITION} and ${RelevantDriverNamesForTests.SOLUTION_ALIGNMENT}" +
                    "\n\n" +
                    "The results per cluster, calculated as the average of all drivers per cluster, indicate that " +
                    "the highest cluster is: ${ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME}." +
                    "\n\n" +
                    "Note that cluster ${ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME} has an overall " +
                    "high influence on the team’s cognitive load despite not including any of the " +
                    "highest drivers mentioned above. This is an area of concern that should be investigated " +
                    "along with the highest drivers."


        val reportTextBuilder = ReportTextBuilder()

        val actualOverviewText =
            reportTextBuilder.buildWhereHighestDriversComeFromText(tclDriverScorePairs, 0.1)

        assertThat(actualOverviewText).isEqualTo(expectedText)
    }

    @Test
    fun `text for 'where do the highest drivers come from' with extra paragraph with 2 clusters in the list`() {
        val tclDriverScorePairs = listOf(
            buildTclDriverScorePair(ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME, 4.7),
            buildTclDriverScorePair(ClusterNamesForTests.WORK_PRACTICES_PROCESSES_CLUSTER_NAME, 4.69),
            buildTclDriverScorePair(ClusterNamesForTests.TASK_CHARACTERISTICS_CLUSTER_NAME, 4.1),
            buildTclDriverScorePair(ClusterNamesForTests.TEAM_CHARACTERISTICS_CLUSTER_NAME, 4.5),
            buildTclDriverScorePair(RelevantDriverNamesForTests.PROBLEM_DEFINITION, 4.7),
            buildTclDriverScorePair(RelevantDriverNamesForTests.SOLUTION_ALIGNMENT, 4.69),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TEAM_COMPLEXITY, 4.5),
            buildTclDriverScorePair(RelevantDriverNamesForTests.TOOL_SUITABILITY, 4.3),
            buildTclDriverScorePair(RelevantDriverNamesForTests.USE_OF_INFORMATION, 4.1)
        )

        val expectedText =
            Term.TeamCognitiveLoadDriver.markUp("Team Cognitive Load drivers") +
                    " are organized in four different ${Term.Cluster.markUp("clusters")}: " +
                    "${Cluster.TeamCharacteristics.markUp()}, ${Cluster.TaskCharacteristics.markUp()}, ${Cluster.WorkPracticesAndProcesses.markUp()}, and ${Cluster.WorkEnvironmentAndTools.markUp()}." +
                    "\n" +
                    "\n" +
                    "The highest drivers highlighted in the Quick summary above are coming from the following cluster:\n\n" +
                    "* Task Characteristics: ${RelevantDriverNamesForTests.PROBLEM_DEFINITION} and ${RelevantDriverNamesForTests.SOLUTION_ALIGNMENT}" +
                    "\n\n" +
                    "The results per cluster, calculated as the average of all drivers per cluster, indicate that " +
                    "the highest clusters are: ${ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME} and " +
                    "${ClusterNamesForTests.WORK_PRACTICES_PROCESSES_CLUSTER_NAME}." +
                    "\n\n" +
                    "Note that clusters ${ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME} and " +
                    "${ClusterNamesForTests.WORK_PRACTICES_PROCESSES_CLUSTER_NAME} have an overall " +
                    "high influence on the team’s cognitive load despite not including any of the " +
                    "highest drivers mentioned above. This is an area of concern that should be investigated " +
                    "along with the highest drivers."


        val reportTextBuilder = ReportTextBuilder()

        val actualOverviewText =
            reportTextBuilder.buildWhereHighestDriversComeFromText(tclDriverScorePairs, 0.1)

        assertThat(actualOverviewText).isEqualTo(expectedText)
    }

    companion object {
        private const val TEAM_CHARACTERISTICS_NAME = "Team Characteristics"
        private const val TASK_CHARACTERISTICS_NAME = "Task Characteristics"
        private const val WORK_PRACTICES_PROCESSES_NAME: String = "Work Practices & Processes"
        private const val WORK_ENVIRONMENT_TOOLS_NAME: String = "Work Environment & Tools"

        val expectedOverviewIntroductionWithTeamName = """
            ### Results
            
            The **current Team Cognitive Load levels** in the four clusters of drivers are **within a reasonable range**.
        """.trimIndent()

        val expectedOverviewResultsText = """
            
            * The levels of TCL in the <cluster entry-key="team-characteristics" cluster-id="team-characteristics">Team Characteristics</cluster> cluster are **within a reasonable range**, with a score of **2.4**.
            * The levels of TCL in the <cluster entry-key="task-characteristics" cluster-id="task-characteristics">Task Characteristics</cluster> cluster are **under control**, with a score of **1.5**.
            * The levels of TCL in the <cluster entry-key="work-practices-and-processes" cluster-id="work-practices-and-processes">Work Practices & Processes</cluster> cluster are **slightly concerning**, with a score of **3.0**.
            * The levels of TCL in the <cluster entry-key="work-environment-and-tools" cluster-id="work-environment-and-tools">Work Environment & Tools</cluster> cluster are **within a reasonable range**, with a score of **2.9**.
        """.trimIndent()

        val expectedTailoredAssessmentWhenSingleAreaNeedsToBeAddressedFirst = """
            The <cluster entry-key="work-practices-and-processes" cluster-id="work-practices-and-processes">Work Practices & Processes</cluster> cluster of drivers stands out today as a **strong influencer of Team Cognitive Load on the team**.
            * To maintain the team's current level of performance, **we recommend addressing this area first.**
            * Leaving it unchecked could lead to an increase in Team Cognitive Load.
            * This, in turn, might significantly affect the team's ability to perform at its current levels.
        """.trimIndent()

        val expectedTailoredAssessmentWhenMultipleAreasNeedToBeAddressedFirst = """
            The <cluster entry-key="team-characteristics" cluster-id="team-characteristics">Team Characteristics</cluster> and <cluster entry-key="task-characteristics" cluster-id="task-characteristics">Task Characteristics</cluster> clusters of drivers stand out today as **strong influencers of Team Cognitive Load on the team**.
            * To maintain the team's current level of performance, **we recommend addressing these areas first.**
            * Leaving them unchecked could lead to an increase in Team Cognitive Load.
            * This, in turn, might significantly affect the team's ability to perform at its current levels.
        """.trimIndent()

        //Parent TCL Drivers
        private val teamCharacteristicsTCLDriverId: UUID = UUID.fromString("8a4411da-a464-4400-99f9-7ae314864734")
        private val taskCharacteristicsTCLDriverId: UUID = UUID.fromString("cab798f0-bf6e-42c4-a4ed-eb94082498ca")
        private val workProcessesPracticesTCLDriverId: UUID = UUID.fromString("f32a81ef-c009-42a4-bab8-1db81ff2dca9")
        private val workEnvironmentToolsTCLDriverId: UUID = UUID.fromString("ea4f414e-cfc1-4478-9056-cb0c1f56eb6f")

        /** Team Characteristics children- mid-level **/
        private val teamCompositionTCLDriverId: UUID = UUID.fromString("eaf6f2b6-4015-4778-a63d-caf2eaa5fecd")

        private val memberRolesTCLDriverId: UUID = UUID.fromString("61b627a6-7d8c-41dc-82d4-f9447002ca0d")
        private val teamCultureTCLDriverId: UUID = UUID.fromString("a791fdf2-1d3c-469f-9cd9-5ae46b2e4c78")

        /** Task Characteristics children  - mid-level **/
        private val problemStatementTCLDriverId: UUID = UUID.fromString("3b524a0f-30bf-4063-b3c3-f83967a8356d")

        private val complexityTCLDriverId: UUID = UUID.fromString("c301780a-6a2c-45fc-98e6-61675d02794a")

        //Metrics - lowest level from Task Characteristics
        private val metricsTCLDriverId: UUID = UUID.fromString("b767e366-a2d0-43fb-ad2d-160c89775deb")

        /** Work Processes & Practices children - mid-level **/
        private val efficiencyEffectivenessTCLDriverId: UUID = UUID.fromString("f54fc8c9-5eae-43e0-8acf-07f90fa7f833")

        private val adaptabilityTCLDriverId: UUID = UUID.fromString("9faeed3f-16e8-486c-b12d-6769d97861c6")

        //Use of information - lowest level from Work Processes & Practices
        private val useOfInformationTCLDriverId: UUID = UUID.fromString("30163a2f-7c2b-4b6f-a0b7-660208843597")

        /**Work Environment & Tools children - mid-level**/
        private val toolsTCLDriverId: UUID = UUID.fromString("c06f269d-75a3-4a3f-985b-46240d8b118b")

        //Environment - lowest level from Work Environment & Tools
        private val environmentTCLDriverId: UUID = UUID.fromString("6841f713-b4a5-49d6-ba14-6b7aa4a9370c")

        /** Assessment id **/
        private val assessmentId: UUID = UUID.fromString("87220a11-fbac-41ea-961b-7cf3cac1e6ed")

        /** SCORE IDs**/
        private val teamCharacteristicsTclDriverScoreId: UUID = UUID.fromString("5345fc51-cb0e-48b3-ac66-76aec39cd379")

        private val taskCharacteristicsTclDriverScoreId: UUID = UUID.fromString("5139dee0-7a75-4fca-a586-dd0c5f159671")
        private val workProcessesPracticesTclDriverScoreId: UUID =
            UUID.fromString("0ac0424d-1353-487d-9f31-bbc330b7941f")
        private val workEnvironmentToolsTclDriverScoreId: UUID = UUID.fromString("5948c880-fd49-4c2f-bb79-beacfd206805")

        private const val TEAM_CHARACTERISTICS_SCORE_VALUE = 2.4
        private const val TASK_CHARACTERISTICS_SCORE_VALUE = 1.5
        private const val WORK_PROCESSES_PRACTICES_SCORE_VALUE = 3.00
        private const val WORK_ENVIRONMENT_TOOLS_SCORE_VALUE = 2.9


        private const val ONE_POINT_FIVE = 1.5
        private const val FOUR_POINT_NINE = 4.9
        private const val THREE_POINT_ZERO = 3.0

        private val teamCharacteristicsDriver = TclDriver(
            id = TclDriverId(teamCharacteristicsTCLDriverId),
            name = TclDriverName(TEAM_CHARACTERISTICS_NAME),
            children = listOf(
                TclDriverId(teamCompositionTCLDriverId),
                TclDriverId(memberRolesTCLDriverId),
                TclDriverId(teamCultureTCLDriverId),
            ),
            parent = null
        )
        private val teamCharacteristicsTclDriverScore = TclDriverScore(
            id = TclDriverScoreId(teamCharacteristicsTclDriverScoreId),
            tclDriverId = TclDriverId(teamCharacteristicsTCLDriverId),
            assessmentId = AssessmentId(assessmentId),
            value = TclDriverScoreValue(TEAM_CHARACTERISTICS_SCORE_VALUE),
        )
        private val teamCharacteristicsTclDriverPair = Pair(
            teamCharacteristicsDriver,
            teamCharacteristicsTclDriverScore

        )

        private val taskCharacteristicsDriver = TclDriver(
            id = TclDriverId(taskCharacteristicsTCLDriverId),
            name = TclDriverName(TASK_CHARACTERISTICS_NAME),
            children = listOf(
                TclDriverId(problemStatementTCLDriverId),
                TclDriverId(complexityTCLDriverId),
                TclDriverId(metricsTCLDriverId),
            ),
            parent = null
        )
        private val taskCharacteristicsTclDriverScore = TclDriverScore(
            id = TclDriverScoreId(taskCharacteristicsTclDriverScoreId),
            tclDriverId = TclDriverId(taskCharacteristicsTCLDriverId),
            assessmentId = AssessmentId(assessmentId),
            value = TclDriverScoreValue(TASK_CHARACTERISTICS_SCORE_VALUE),
        )
        private val taskCharacteristicsTclDriverPair = Pair(
            taskCharacteristicsDriver,
            taskCharacteristicsTclDriverScore

        )

        private val workProcessesPracticesDriver = TclDriver(
            id = TclDriverId(workProcessesPracticesTCLDriverId),
            name = TclDriverName(WORK_PRACTICES_PROCESSES_NAME),
            children = listOf(
                TclDriverId(useOfInformationTCLDriverId),
                TclDriverId(efficiencyEffectivenessTCLDriverId),
                TclDriverId(adaptabilityTCLDriverId),
            ),
            parent = null
        )
        private val workProcessesPracticesTclDriverScore = TclDriverScore(
            id = TclDriverScoreId(workProcessesPracticesTclDriverScoreId),
            tclDriverId = TclDriverId(workProcessesPracticesTCLDriverId),
            assessmentId = AssessmentId(assessmentId),
            value = TclDriverScoreValue(WORK_PROCESSES_PRACTICES_SCORE_VALUE),
        )
        private val workProcessesPracticesTclDriverPair = Pair(
            workProcessesPracticesDriver,
            workProcessesPracticesTclDriverScore
        )

        private val workEnvironmentToolsTclDriver = TclDriver(
            id = TclDriverId(workEnvironmentToolsTCLDriverId),
            name = TclDriverName(WORK_ENVIRONMENT_TOOLS_NAME),
            children = listOf(
                TclDriverId(toolsTCLDriverId),
                TclDriverId(environmentTCLDriverId),
            ),
            parent = null
        )

        private val workEnvironmentToolsTclDriverScore = TclDriverScore(
            id = TclDriverScoreId(workEnvironmentToolsTclDriverScoreId),
            tclDriverId = TclDriverId(workEnvironmentToolsTCLDriverId),
            assessmentId = AssessmentId(assessmentId),
            value = TclDriverScoreValue(WORK_ENVIRONMENT_TOOLS_SCORE_VALUE),
        )
        private val workEnvironmentToolsTclDriverPair = Pair(
            workEnvironmentToolsTclDriver,
            workEnvironmentToolsTclDriverScore
        )

        private fun buildTclDriverScorePair(name: String, score: Double) = Pair(
            TclDriver(
                id = TclDriverId(UUID.randomUUID()),
                name = TclDriverName(name),
                children = listOf(),
                parent = null
            ),
            TclDriverScore(
                id = TclDriverScoreId(UUID.randomUUID()),
                tclDriverId = TclDriverId(UUID.randomUUID()),
                assessmentId = AssessmentId(UUID.randomUUID()),
                value = TclDriverScoreValue(score)
            )
        )

        val tclClusterScorePairsWithinReasonableRange = listOf(
            teamCharacteristicsTclDriverPair,
            taskCharacteristicsTclDriverPair,
            workProcessesPracticesTclDriverPair,
            workEnvironmentToolsTclDriverPair
        )

        val multipleTclClusterScorePairsThatNeedToBeAddressed = listOf(
            teamCharacteristicsTclDriverPair
                .copy(second = teamCharacteristicsTclDriverScore.copy(value = TclDriverScoreMother.value(3.0))),
            taskCharacteristicsTclDriverPair
                .copy(second = taskCharacteristicsTclDriverScore.copy(value = TclDriverScoreMother.value(4.2))),
            workProcessesPracticesTclDriverPair
                .copy(second = workProcessesPracticesTclDriverScore.copy(value = TclDriverScoreMother.value(2.2))),
            workEnvironmentToolsTclDriverPair
                .copy(second = workEnvironmentToolsTclDriverScore.copy(value = TclDriverScoreMother.value(1.2))),
        )

        val tclClusterScorePairsWithRandomNames = listOf(
            teamCharacteristicsTclDriverPair.copy(
                teamCharacteristicsDriver.copy(
                    name = TclDriverName(TEAM_CHARACTERISTICS_NAME)
                )
            ),
            taskCharacteristicsTclDriverPair.copy(
                taskCharacteristicsDriver.copy(
                    name = TclDriverName(TASK_CHARACTERISTICS_NAME)
                )
            ),
            workProcessesPracticesTclDriverPair.copy(
                workProcessesPracticesDriver.copy(
                    name = TclDriverName(WORK_PRACTICES_PROCESSES_NAME)
                )
            ),
            workEnvironmentToolsTclDriverPair.copy(
                workEnvironmentToolsTclDriver.copy(
                    name = TclDriverName(WORK_ENVIRONMENT_TOOLS_NAME)
                )
            )
        )

        val tclClusterScorePairsSlightlyConcerning = listOf(
            Pair(
                TclDriver(
                    id = TclDriverId(teamCharacteristicsTCLDriverId),
                    name = TclDriverName(TEAM_CHARACTERISTICS_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(teamCompositionTCLDriverId),
                        TclDriverId(memberRolesTCLDriverId),
                        TclDriverId(teamCultureTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(teamCharacteristicsTclDriverScoreId),
                    tclDriverId = TclDriverId(teamCharacteristicsTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(FOUR_POINT_NINE),
                )
            ),
            Pair(
                TclDriver(
                    id = TclDriverId(taskCharacteristicsTCLDriverId),
                    name = TclDriverName(TASK_CHARACTERISTICS_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(problemStatementTCLDriverId),
                        TclDriverId(complexityTCLDriverId),
                        TclDriverId(metricsTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(taskCharacteristicsTclDriverScoreId),
                    tclDriverId = TclDriverId(taskCharacteristicsTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(FOUR_POINT_NINE),
                )

            ),
            Pair(
                TclDriver(
                    id = TclDriverId(workProcessesPracticesTCLDriverId),
                    name = TclDriverName(WORK_PRACTICES_PROCESSES_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(useOfInformationTCLDriverId),
                        TclDriverId(efficiencyEffectivenessTCLDriverId),
                        TclDriverId(adaptabilityTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(workProcessesPracticesTclDriverScoreId),
                    tclDriverId = TclDriverId(workProcessesPracticesTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(FOUR_POINT_NINE),
                )
            ),
            Pair(
                TclDriver(
                    id = TclDriverId(workEnvironmentToolsTCLDriverId),
                    name = TclDriverName(WORK_ENVIRONMENT_TOOLS_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(toolsTCLDriverId),
                        TclDriverId(environmentTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(workEnvironmentToolsTclDriverScoreId),
                    tclDriverId = TclDriverId(workEnvironmentToolsTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(THREE_POINT_ZERO),
                )
            )
        )
        val tclClusterScorePairsUnderControl = listOf(
            Pair(
                TclDriver(
                    id = TclDriverId(teamCharacteristicsTCLDriverId),
                    name = TclDriverName(TEAM_CHARACTERISTICS_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(teamCompositionTCLDriverId),
                        TclDriverId(memberRolesTCLDriverId),
                        TclDriverId(teamCultureTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(teamCharacteristicsTclDriverScoreId),
                    tclDriverId = TclDriverId(teamCharacteristicsTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(ONE_POINT_FIVE),
                )

            ),
            Pair(
                TclDriver(
                    id = TclDriverId(taskCharacteristicsTCLDriverId),
                    name = TclDriverName(TASK_CHARACTERISTICS_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(problemStatementTCLDriverId),
                        TclDriverId(complexityTCLDriverId),
                        TclDriverId(metricsTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(taskCharacteristicsTclDriverScoreId),
                    tclDriverId = TclDriverId(taskCharacteristicsTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(ONE_POINT_FIVE),
                )

            ),
            Pair(
                TclDriver(
                    id = TclDriverId(workProcessesPracticesTCLDriverId),
                    name = TclDriverName(WORK_PRACTICES_PROCESSES_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(useOfInformationTCLDriverId),
                        TclDriverId(efficiencyEffectivenessTCLDriverId),
                        TclDriverId(adaptabilityTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(workProcessesPracticesTclDriverScoreId),
                    tclDriverId = TclDriverId(workProcessesPracticesTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(ONE_POINT_FIVE),
                )
            ),
            Pair(
                TclDriver(
                    id = TclDriverId(workEnvironmentToolsTCLDriverId),
                    name = TclDriverName(WORK_ENVIRONMENT_TOOLS_NAME),
                    children = listOf<TclDriverId>(
                        TclDriverId(toolsTCLDriverId),
                        TclDriverId(environmentTCLDriverId),
                    ),
                    parent = null
                ),
                TclDriverScore(
                    id = TclDriverScoreId(workEnvironmentToolsTclDriverScoreId),
                    tclDriverId = TclDriverId(workEnvironmentToolsTCLDriverId),
                    assessmentId = AssessmentId(assessmentId),
                    value = TclDriverScoreValue(ONE_POINT_FIVE),
                )
            )
        )
    }
}
