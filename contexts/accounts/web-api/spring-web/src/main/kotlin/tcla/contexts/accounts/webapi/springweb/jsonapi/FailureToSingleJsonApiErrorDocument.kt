package tcla.contexts.accounts.webapi.springweb.jsonapi

import tcla.contexts.accounts.core.application.failures.Failure
import tcla.libraries.jsonapi.ErrorDocument

fun Failure.toSingleJsonApiErrorDocument(): ErrorDocument =
    ErrorDocument(errors = listOf(toJsonApiError(this)))
