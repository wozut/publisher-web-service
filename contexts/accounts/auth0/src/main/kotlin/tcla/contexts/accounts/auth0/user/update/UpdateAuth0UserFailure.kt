package tcla.contexts.accounts.auth0.user.update

sealed class UpdateAuth0UserFailure {
    data object Auth0UserNotFound : UpdateAuth0UserFailure()
    data object ExpiredToken : UpdateAuth0UserFailure()

    data class TechnicalException(val throwable: Throwable) : UpdateAuth0UserFailure()
}
