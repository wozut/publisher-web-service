package tcla.libraries.uuidvalidation

import arrow.core.Either
import java.util.UUID

fun String.toUuid(): Either<StringNotConformsUuid, UUID> =
    Either.catch { UUID.fromString(this) }.mapLeft { StringNotConformsUuid }

