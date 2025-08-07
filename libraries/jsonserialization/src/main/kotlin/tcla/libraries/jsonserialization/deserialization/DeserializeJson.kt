package tcla.libraries.jsonserialization.deserialization

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import tcla.libraries.jsonserialization.MOSHI
import kotlin.reflect.KClass

fun <T : Any> String.deserializeJsonAs(type: KClass<T>): Either<DeserializeJsonFailure.JsonStructureNotMatchObject, T> =
    Either.catch { MOSHI.adapter(type.java) }
        .mapLeft { DeserializeJsonFailure.BuildAdapter }
        .flatMap { it.fromJson(this)!!.right() }
        .mapLeft { DeserializeJsonFailure.JsonStructureNotMatchObject }
