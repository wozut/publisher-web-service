package tcla.contexts.accounts.auth0.gettokenanduser

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.auth0.json.mgmt.users.User
import okhttp3.OkHttpClient
import tcla.contexts.accounts.auth0.accesstoken.getcurrentaccesstoken.GetCurrentAccessTokenFailure
import tcla.contexts.accounts.auth0.accesstoken.getcurrentaccesstoken.getCurrentAccessToken
import tcla.contexts.accounts.auth0.user.get.GetAuth0UserFailure
import tcla.contexts.accounts.auth0.user.get.getAuth0User
import tcla.contexts.accounts.core.domain.account.model.Account
import tcla.contexts.accounts.core.domain.account.model.AccountId
import tcla.contexts.accounts.core.domain.account.model.Email
import tcla.contexts.accounts.core.domain.account.model.LoginsCount
import tcla.contexts.accounts.core.domain.account.model.Name

fun getTokenAndUser(
    domain: String,
    id: String,
    clientId: String,
    clientSecret: String,
    audience: String,
    getAccessTokenUrl: String,
    okHttpClient: OkHttpClient
): Either<GetTokenAndUserFailure, Account> =
    getCurrentAccessToken(
        clientId = clientId,
        clientSecret = clientSecret,
        audience = audience,
        getAccessTokenUrl = getAccessTokenUrl,
        okHttpClient = okHttpClient
    ).mapLeft {
        when (it) {
            is GetCurrentAccessTokenFailure.RefreshToken -> GetTokenAndUserFailure.RefreshToken
        }
    }.flatMap { accessToken ->
        getAuth0User(domain = domain, id = id, accessToken = accessToken)
            .mapLeft {
                when (it) {
                    GetAuth0UserFailure.Auth0UserNotFound -> GetTokenAndUserFailure.Auth0UserNotFound
                    GetAuth0UserFailure.ExpiredToken -> GetTokenAndUserFailure.ExpiredToken
                    is GetAuth0UserFailure.TechnicalException -> GetTokenAndUserFailure.TechnicalException(it.throwable)
                }
            }
    }.flatMap { auth0User -> auth0User.toAccount().right() }

private fun User.toAccount(): Account =
    Account(
        id = AccountId(id),
        name = Name(name),
        loginsCount = LoginsCount(loginsCount),
        email = Email(email)
    )
