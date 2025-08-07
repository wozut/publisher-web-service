package tcla.contexts.accounts.webapi.springweb.jsonapi

import org.springframework.http.HttpStatus
import tcla.contexts.accounts.core.application.failures.Failure
import tcla.contexts.accounts.core.application.failures.FindAccountFailure
import tcla.contexts.accounts.core.application.failures.GetNewAccessTokenFailure

fun Failure.toStatusCode(): HttpStatus = when (this) {
    is FindAccountFailure -> when(this) {
        FindAccountFailure.NotFound -> TODO()
        FindAccountFailure.RequestNotAuthenticated -> TODO()
        is FindAccountFailure.TechnicalException -> TODO()
        FindAccountFailure.Unauthorized -> TODO()
    }
    is GetNewAccessTokenFailure -> when(this) {
        is GetNewAccessTokenFailure.TechnicalException -> TODO()
        GetNewAccessTokenFailure.UnexpectedJsonStructure -> TODO()
        GetNewAccessTokenFailure.UnexpectedObjectStructure -> TODO()
        GetNewAccessTokenFailure.UnsuccessfulResponse -> TODO()
    }

    Failure.RequestNotAuthenticated -> TODO()
}
