package tcla.apigateway.jwt

sealed class ValidateJwtFailure {
    data object InvalidAudience : ValidateJwtFailure()
    data class Invalid(val throwable: Throwable) : ValidateJwtFailure()
}
