package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.core.domain.report.model.Name
import tcla.contexts.tcla.core.domain.report.model.Score
import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverMother
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverName
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreMother
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreValue
import java.util.UUID


class FindRelevantDriverScorePairsWithHighestScoreKtTest {

    @Test
    fun `it filters and sorts the highest drivers within a range`() {
        val tclDriversInTeamCharacteristics = tclDriverAndScorePairs(
            ClusterNamesForTests.TEAM_CHARACTERISTICS_CLUSTER_NAME,
            RelevantDriverNamesForTests.TEAM_COMPLEXITY,
            3.2
        )
        val tclDriversInTaskCharacteristics = tclDriverAndScorePairs(
            ClusterNamesForTests.TASK_CHARACTERISTICS_CLUSTER_NAME,
            RelevantDriverNamesForTests.PROBLEM_DEFINITION,
            3.3
        )
        val firstHighestScore = 3.5
        val tclDriversInWorkProcessesPracticesClusterName = tclDriverAndScorePairs(
            ClusterNamesForTests.WORK_PRACTICES_PROCESSES_CLUSTER_NAME,
            RelevantDriverNamesForTests.USE_OF_INFORMATION,
            firstHighestScore
        )
        val secondHighestScore = 3.4
        val tclDriversInWorkEnvironmentToolsClusterName = tclDriverAndScorePairs(
            ClusterNamesForTests.WORK_ENVIRONMENT_TOOLS_CLUSTER_NAME,
            RelevantDriverNamesForTests.TOOL_SUITABILITY,
            secondHighestScore
        )

        val allDrivers = tclDriversInTeamCharacteristics
            .plus(tclDriversInTaskCharacteristics)
            .plus(tclDriversInWorkProcessesPracticesClusterName)
            .plus(tclDriversInWorkEnvironmentToolsClusterName)

        val highestSecondLevelDrivers = allDrivers.findRelevantDriverScorePairsWithHighestScore(0.2)

        assertThat(highestSecondLevelDrivers).containsExactly(
            *listOf(
                TclDriverAndScore(id = Driver.UseOfInformation.id, name = Name(RelevantDriverNamesForTests.USE_OF_INFORMATION), score = Score(3.5)),
                TclDriverAndScore(id = Driver.ToolSuitability.id, name = Name(RelevantDriverNamesForTests.TOOL_SUITABILITY), score = Score(3.4)),
                TclDriverAndScore(id = Driver.ProblemDefinition.id, name = Name(RelevantDriverNamesForTests.PROBLEM_DEFINITION), score = Score(3.3))
            ).toTypedArray()
        )
    }

    private fun tclDriverAndScorePairs(
        clusterName: String,
        driverName: String,
        scoreForDriver: Double
    ): List<Pair<TclDriver, TclDriverScore>> {
        val clusterA = TclDriverMother.default(
            name = TclDriverName(clusterName),
            children = listOf(),
            parent = null
        )
        val driverA1 = TclDriverMother.default(
            name = TclDriverName(UUID.randomUUID().toString()),
            children = listOf(),
            parent = clusterA.id
        )
        clusterA.addChild(driverA1.id)
        val driverA1A = TclDriverMother.default(
            name = TclDriverName(driverName),
            children = listOf(),
            parent = driverA1.id
        )
        driverA1.addChild(driverA1A.id)
        return listOf(
            Pair(
                clusterA, TclDriverScoreMother.default(
                    tclDriverId = clusterA.id,
                    value = TclDriverScoreValue(3.0)
                )
            ),
            Pair(
                driverA1,
                TclDriverScoreMother.default(
                    tclDriverId = driverA1.id,
                    value = TclDriverScoreValue(3.0)
                )
            ),
            Pair(
                driverA1A,
                TclDriverScoreMother.default(
                    tclDriverId = driverA1A.id,
                    value = TclDriverScoreValue(scoreForDriver)
                )
            )
        )
    }
}
