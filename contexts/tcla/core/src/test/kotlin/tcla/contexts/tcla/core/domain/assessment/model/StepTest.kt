package tcla.contexts.tcla.core.domain.assessment.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StepTest {
    @Test
    fun `steps from which can cancel`() {
        val expectedSteps = setOf(Step.Scheduled, Step.CollectingData)
        Step.stepsFromWhichCanCancel().let { actualSteps ->
            assertThat(actualSteps).containsExactlyInAnyOrderElementsOf(expectedSteps)
        }
    }

    @Test
    fun `get all steps`() {
        val expectedSteps = setOf(
            Step.Scheduled,
            Step.CollectingData,
            Step.AnalysableData,
            Step.ResultsAvailable,
            Step.Canceled,
            Step.DataCollected,
            Step.DataNotAnalysable
        )

        Step.allSteps().let { actualSteps ->
            assertThat(actualSteps).containsExactlyInAnyOrderElementsOf(expectedSteps)
        }
    }
}