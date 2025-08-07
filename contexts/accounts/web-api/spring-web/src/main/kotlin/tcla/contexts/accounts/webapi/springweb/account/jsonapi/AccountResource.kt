package tcla.contexts.accounts.webapi.springweb.account.jsonapi

import tcla.contexts.accounts.core.domain.account.model.Account

data class AccountResource(
    val id: String,
    val attributes: AccountResourceAttributes
) {
    val type: String = ACCOUNT_JSON_API_TYPE
}

fun Account.toResource(): AccountResource =
    AccountResource(
        id = id.string,
        attributes = AccountResourceAttributes(
            name = name.string,
            loginsCount = loginsCount.int,
            email = email.string
        )
    )
