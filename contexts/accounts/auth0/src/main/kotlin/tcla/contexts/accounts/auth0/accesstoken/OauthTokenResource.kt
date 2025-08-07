package tcla.contexts.accounts.auth0.accesstoken

data class OauthTokenResource(
    val access_token: String,
    val scope: String,
    val expires_in: Int,
    val token_type: String,
)
