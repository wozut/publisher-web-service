package tcla.contexts.analytics.jsonapi

import tcla.contexts.analytics.Failure
import tcla.contexts.analytics.FailureClassAndCodeMapping
import tcla.libraries.jsonapi.Error

fun toJsonApiError(failure: Failure): Error = Error(
    status = failure.toStatusCode().value().toString(),
    code = "${FailureClassAndCodeMapping.getCodeFor(failure::class)}",
    title = failure.humanReadableSummary
)
