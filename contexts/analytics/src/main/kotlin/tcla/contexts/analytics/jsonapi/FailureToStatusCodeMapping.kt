package tcla.contexts.analytics.jsonapi

import org.springframework.http.HttpStatus
import tcla.contexts.analytics.CreateOnboardingFailure
import tcla.contexts.analytics.Failure

fun Failure.toStatusCode(): HttpStatus = when (this) {
    is Failure.DatabaseException -> HttpStatus.BAD_GATEWAY
    is Failure.EntityNotFound -> HttpStatus.NOT_FOUND
    Failure.RequestNotAuthenticated -> HttpStatus.UNAUTHORIZED
    Failure.InvalidId -> HttpStatus.BAD_REQUEST
    CreateOnboardingFailure.AtMostOneOnboardingPerAccount -> HttpStatus.FORBIDDEN
    Failure.InvalidDocument -> HttpStatus.BAD_REQUEST
    Failure.JsonApiTypeNotAllowed -> HttpStatus.BAD_REQUEST
}
