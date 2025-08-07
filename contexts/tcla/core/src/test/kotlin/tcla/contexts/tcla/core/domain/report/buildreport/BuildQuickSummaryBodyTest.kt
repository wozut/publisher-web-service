package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.report.model.BodyPart
import tcla.contexts.tcla.core.domain.report.model.Driver

class BuildQuickSummaryBodyTest {
    @Test
    fun `it shows a paragraph when there is 1 driver`() {
        val tclDriverNames = listOf(
            Driver.TeamComplexity.name
        )

        val actualBody: List<BodyPart> = BuildQuickSummaryBody().execute(
            tclDriverNames = tclDriverNames,
            tooManyHighDriversThreshold = 4
        )

        val expectedText =
            "In this assessment, the highest" +
                    " <term entry-key=\"team-cognitive-load-driver\">Team Cognitive Load driver</term> for this team is" +
                    " <driver entry-key=\"team-complexity\" cluster-id=\"team-characteristics\">Team Complexity</driver>." +
                    " While other drivers might be negatively impacting the team as well," +
                    " this 1 driver should probably be investigated first." +
                    " If this driver is left unchecked, the Team Cognitive Load is likely to grow and impair" +
                    " the team’s ability to perform adequately in the future."

        assertThat(actualBody.first().text.value)
            .isEqualTo(expectedText)

        assertThat(actualBody)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(listOf(BodyPart(
                text = BodyPart.Text(expectedText),
                order = BodyPart.Order(1)
            )))
    }

    @Test
    fun `it shows a paragraph when there are up to threshold amount drivers`() {
        val tclDriverNames = listOf(
            Driver.TeamComplexity.name,
            Driver.ProblemDefinition.name,
            Driver.UseOfInformation.name,
            Driver.ToolSuitability.name
        )

        val actualBody: List<BodyPart> = BuildQuickSummaryBody().execute(
            tclDriverNames = tclDriverNames,
            tooManyHighDriversThreshold = 4
        )

        val expectedText =
            "In this assessment, the highest" +
                    " <term entry-key=\"team-cognitive-load-driver\">Team Cognitive Load drivers</term> for this team are" +
                    " <driver entry-key=\"team-complexity\" cluster-id=\"team-characteristics\">Team Complexity</driver>," +
                    " <driver entry-key=\"problem-definition\" cluster-id=\"task-characteristics\">Problem Definition</driver>," +
                    " <driver entry-key=\"use-of-information\" cluster-id=\"work-practices-and-processes\">Use Of Information</driver>," +
                    " and <driver entry-key=\"tool-suitability\" cluster-id=\"work-environment-and-tools\">Tool Suitability</driver>." +
                    " While other drivers might be negatively impacting the team as well," +
                    " these 4 drivers should probably be investigated first." +
                    " If these drivers are left unchecked, the Team Cognitive Load is likely to grow and impair" +
                    " the team’s ability to perform adequately in the future."

        assertThat(actualBody.first().text.value)
            .isEqualTo(expectedText)

        assertThat(actualBody)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(listOf(BodyPart(
                text = BodyPart.Text(expectedText),
                order = BodyPart.Order(1)
            )))
    }

    @Test
    fun `it shows a paragraph when there are more than threshold amount drivers`() {
        val tclDriverNames = listOf(
            Driver.TeamComplexity.name,
            Driver.ProblemDefinition.name,
            Driver.UseOfInformation.name,
            Driver.ToolSuitability.name,
            Driver.Resilience.name
        )

        val actualBody: List<BodyPart> = BuildQuickSummaryBody().execute(
            tclDriverNames = tclDriverNames,
            tooManyHighDriversThreshold = 4
        )

        val expectedText =
            "In this assessment, 5 different" +
                    " <term entry-key=\"team-cognitive-load-driver\">Team Cognitive Load drivers</term>" +
                    " were found to be strongly influencing" +
                    " the team’s cognitive load. Given this is an unusual number of highest drivers," +
                    " the team probably should **invest the time now to understand** their environment and map" +
                    " out potential causes for their highest drivers." +
                    "\n" +
                    "\n" +
                    "If most of the drivers are coming from the same" +
                    " <term entry-key=\"cluster\">cluster</term> (as detailed in the next section)" +
                    " that might be a good starting point. If the highest drivers are spread over multiple clusters " +
                    "then walk through all the drivers and decide which ones should/could be addressed first. " +
                    "We strongly recommend to re-assess the team’s cognitive load after some improvement actions " +
                    "have been taken.\n" +
                    "\n" +
                    "\n" +
                    "**Beware that the number of reported highest drivers puts the performance and well-being of the" +
                    " team at risk right now**."

        assertThat(actualBody.first().text.value)
            .isEqualTo(expectedText)

        assertThat(actualBody)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(listOf(BodyPart(
                text = BodyPart.Text(expectedText),
                order = BodyPart.Order(1)
            )))
    }
}
