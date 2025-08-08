package tcla.contexts.accounts.auth0.accesstoken.getnewaccesstoken

sealed class GetNewAccessTokenFailure {
    data object UnsuccessfulResponse : GetNewAccessTokenFailure()
    data object UnexpectedJsonStructure : GetNewAccessTokenFailure()
    data object UnexpectedObjectStructure : GetNewAccessTokenFailure()
    data class TechnicalException(val throwable: Throwable) : GetNewAccessTokenFailure()
}
