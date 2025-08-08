package tcla.contexts.accounts.core.application.account.update

sealed class UpdateAccountFailure {
    data object NameMustNotBeNullNeitherBlank : UpdateAccountFailure()
    data object RequestNotAuthenticated : UpdateAccountFailure()
    data object NameMustBeDifferentFromEmail : UpdateAccountFailure()

}
