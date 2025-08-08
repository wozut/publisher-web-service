package tcla.contexts.analytics.jsonapi

import arrow.core.NonEmptyList
import org.springframework.http.ResponseEntity
import tcla.contexts.analytics.Failure

fun NonEmptyList<Failure>.toFailureResponse(): ResponseEntity<Any> =
    map(::toJsonApiError).toMultipleJsonApiErrorResponse()
