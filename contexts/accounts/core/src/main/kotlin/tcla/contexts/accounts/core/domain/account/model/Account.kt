package tcla.contexts.accounts.core.domain.account.model

data class Account(
    val id: AccountId,
    val name: Name,
    val loginsCount: LoginsCount,
    val email: Email
) {
    fun updateName(name: Name): Account = copy(name = name)
}
