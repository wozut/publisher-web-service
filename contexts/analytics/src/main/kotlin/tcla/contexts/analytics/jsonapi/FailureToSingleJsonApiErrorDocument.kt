package tcla.contexts.analytics.jsonapi

import tcla.contexts.analytics.Failure
import tcla.libraries.jsonapi.ErrorDocument

fun Failure.toSingleJsonApiErrorDocument(): ErrorDocument =
    ErrorDocument(errors = listOf(toJsonApiError(this)))
