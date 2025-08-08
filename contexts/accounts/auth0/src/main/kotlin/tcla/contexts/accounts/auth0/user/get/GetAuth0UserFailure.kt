package tcla.contexts.accounts.auth0.user.get

sealed class GetAuth0UserFailure {
    data object Auth0UserNotFound : GetAuth0UserFailure()
    data object ExpiredToken : GetAuth0UserFailure()

    data class TechnicalException(val throwable: Throwable) : GetAuth0UserFailure()
}
