package tcla.contexts.tcla.core.domain.questionnaire.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ResponsesTest {

    @Test
    fun `minimum response logic`() {
        val parameters = listOf(
            Triple(5, 1.0, 5),
            Triple(5, 0.8, 4),
            Triple(5, 0.6, 3),
            Triple(3, 0.6, 3),
        )
        parameters.forEach {
            val responses = Responses(
                maximumAmountToBeCollected = Responses.MaximumAmountToBeCollected(it.first),
                minimumRateRequired = Responses.MinimumRateRequired(it.second),
                collection = emptySet()
            )
            assertEquals(it.third, responses.minimumAmountRequired)
        }
    }
}