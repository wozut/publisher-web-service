package tcla.contexts.accounts.auth0.gettokenanduser

sealed class GetTokenAndUserFailure {
    data object RefreshToken : GetTokenAndUserFailure()
    data object Auth0UserNotFound : GetTokenAndUserFailure()
    data object ExpiredToken : GetTokenAndUserFailure()
    data object TokenNotSet : GetTokenAndUserFailure()
    data class TechnicalException(val throwable: Throwable) : GetTokenAndUserFailure()
}
