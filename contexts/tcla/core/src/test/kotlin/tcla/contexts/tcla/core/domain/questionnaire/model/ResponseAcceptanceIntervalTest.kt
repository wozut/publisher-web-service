package tcla.contexts.tcla.core.domain.questionnaire.model

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Instant

class ResponseAcceptanceIntervalTest {
    @ParameterizedTest
    @CsvSource(
        "2023-02-01T10:00:00Z, 2023-02-02T10:00:00Z, 2023-02-03T10:00:00Z, 2023-02-04T10:00:00Z",
        "2023-02-03T10:00:00Z, 2023-02-04T10:00:00Z, 2023-02-01T10:00:00Z, 2023-02-02T10:00:00Z"
    )
    fun `it recognizes correctly intervals that DO NOT overlap`(startDateA: String, endDateA: String, startDateB: String, endDateB: String) {
        val intervalA = buildValidResponseAcceptanceInterval(startDateA, endDateA)
        val intervalB = buildValidResponseAcceptanceInterval(startDateB, endDateB)

        val overlaps = intervalA.overlaps(intervalB)

        assertThat(overlaps).isFalse()
    }

    @ParameterizedTest
    @CsvSource(
        "2023-02-02T10:00:00Z, 2023-02-04T10:00:00Z, 2023-02-03T10:00:00Z, 2023-02-05T10:00:00Z",
        "2023-02-02T10:00:00Z, 2023-02-04T10:00:00Z, 2023-02-01T10:00:00Z, 2023-02-03T10:00:00Z",
        "2023-02-02T10:00:00Z, 2023-02-04T10:00:00Z, 2023-02-01T10:00:00Z, 2023-02-02T10:00:00Z",
        "2023-02-02T10:00:00Z, 2023-02-04T10:00:00Z, 2023-02-04T10:00:00Z, 2023-02-05T10:00:00Z"
    )
    fun `it recognizes correctly intervals that DO overlap`(startDateA: String, endDateA: String, startDateB: String, endDateB: String) {
        val intervalA = buildValidResponseAcceptanceInterval(startDateA, endDateA)
        val intervalB = buildValidResponseAcceptanceInterval(startDateB, endDateB)

        val overlaps = intervalA.overlaps(intervalB)

        assertThat(overlaps).isTrue()
    }

    private fun buildValidResponseAcceptanceInterval(
        startDate: String,
        endDate: String
    ): ResponseAcceptanceInterval {
        val startInstantA = Instant.parse(startDate)
        val endInstantA = Instant.parse(endDate)
        return ResponseAcceptanceInterval(startInstantA, endInstantA).fold(
            { Assertions.fail("It must be right") },
            { it })
    }
}
