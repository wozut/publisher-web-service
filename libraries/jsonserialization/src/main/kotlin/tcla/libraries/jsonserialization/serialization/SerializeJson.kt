package tcla.libraries.jsonserialization.serialization

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import tcla.libraries.jsonserialization.MOSHI


fun Any.serializeToJson(): Either<SerializeToJsonFailure, String> =
    Either.catch { MOSHI.adapter(this.javaClass) }
        .mapLeft { SerializeToJsonFailure.BuildAdapter }
        .flatMap { jsonAdapter -> jsonAdapter.toJson(this).right() }
        .mapLeft { SerializeToJsonFailure.ToJson }

