package tcla.contexts.accounts.core.domain.account

sealed class SaveAccountFailure {
    data class GetCurrentAccessToken(val exception: Throwable) : SaveAccountFailure()
    data object UnableToGetNewAccessToken : SaveAccountFailure()

}
