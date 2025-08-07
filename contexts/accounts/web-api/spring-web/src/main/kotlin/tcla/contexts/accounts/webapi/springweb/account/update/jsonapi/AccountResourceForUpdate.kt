package tcla.contexts.accounts.webapi.springweb.account.update.jsonapi

data class AccountResourceForUpdate(
    val type: String,
    val id: String,
    val attributes: Map<String, String?>?
)
