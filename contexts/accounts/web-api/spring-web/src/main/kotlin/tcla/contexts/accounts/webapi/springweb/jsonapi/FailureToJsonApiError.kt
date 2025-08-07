package tcla.contexts.accounts.webapi.springweb.jsonapi

import tcla.contexts.accounts.core.application.failures.Failure
import tcla.contexts.accounts.core.application.failures.FailureClassAndCodeMapping
import tcla.libraries.jsonapi.Error

fun toJsonApiError(failure: Failure): Error = Error(
    status = failure.toStatusCode().value().toString(),
    code = "${FailureClassAndCodeMapping.getCodeFor(failure::class)}",
    title = failure.humanReadableSummary
)
