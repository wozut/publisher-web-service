package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.report.model.Cluster

class ClusterSectionBuilderTest {
    @Test
    fun `Team characteristics section should have an introduction`() {

        val clusterSectionBuilder = ClusterSectionBuilder()
        val actualIntroductionBody = clusterSectionBuilder.buildIntroductionBody(Cluster.TeamCharacteristics)
        val actualIntroductionTitle = clusterSectionBuilder.buildIntroductionTitle()

        assertThat(actualIntroductionTitle.plus("\n").plus("\n").plus(actualIntroductionBody)).isEqualTo(
            expectedIntroduction
        )
    }

    companion object {
        val expectedIntroduction = """
            #### Key factors influencing Team Cognitive Load in this cluster
            
            The characteristics of a team, including team member roles and team culture, directly affect Team Cognitive Load. Ambiguity in roles, ineffective communication, and a lack of trust can cause team members to feel overwhelmed and frustrated, increasing cognitive load.
            
        """.trimIndent()
    }
}
