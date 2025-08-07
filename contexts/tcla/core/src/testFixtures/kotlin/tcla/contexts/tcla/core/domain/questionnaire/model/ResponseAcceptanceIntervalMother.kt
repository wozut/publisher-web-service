package tcla.contexts.tcla.core.domain.questionnaire.model

import java.time.Duration
import java.time.Instant

object ResponseAcceptanceIntervalMother {
    fun default(
        start: Instant = Instant.parse("2023-12-01T00:00:00Z"),
        end: Instant = start.plus(Duration.ofDays(2)),
    ): ResponseAcceptanceInterval = ResponseAcceptanceInterval(
        start = start,
        end = end
    ).fold({ TODO() }, { it })
}
