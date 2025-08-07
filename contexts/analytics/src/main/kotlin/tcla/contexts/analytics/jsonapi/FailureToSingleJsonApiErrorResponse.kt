package tcla.contexts.analytics.jsonapi

import org.springframework.http.ResponseEntity
import tcla.contexts.analytics.Failure

fun Failure.toSingleJsonApiErrorResponse(): ResponseEntity<Any> =
    ResponseEntity(toSingleJsonApiErrorDocument(), toStatusCode())
