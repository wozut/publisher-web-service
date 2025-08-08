package tcla.contexts.tcla.database.springdatajpa

import arrow.core.Either
import tcla.contexts.tcla.core.application.failures.InstantToTimestampFailure
import java.sql.Timestamp
import java.time.Instant

fun Instant.toTimestamp(): Either<InstantToTimestampFailure, Timestamp> {
    return Either.catch { Timestamp.from(this) }
        .mapLeft { InstantToTimestampFailure.InstantIsTooLargeToRepresentAsTimestamp }
}
