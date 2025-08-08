package tcla.contexts.accounts.webapi.springweb.account.jsonapi

data class AccountResourceAttributes(
    val name: String,
    val loginsCount: Int,
    val email: String
)
