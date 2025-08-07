package tcla.contexts.accounts.auth0.accesstoken.refreshtoken

sealed class RefreshTokenFailure {
    data object UnsuccessfulResponse : RefreshTokenFailure()
    data object UnexpectedJsonStructure : RefreshTokenFailure()
    data object UnexpectedObjectStructure : RefreshTokenFailure()
    data class TechnicalException(val throwable: Throwable) : RefreshTokenFailure()
}
