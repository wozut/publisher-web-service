package tcla.contexts.accounts.auth0.accesstoken.refreshtoken

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import okhttp3.OkHttpClient
import tcla.contexts.accounts.auth0.accesstoken.getnewaccesstoken.GetNewAccessTokenFailure
import tcla.contexts.accounts.auth0.accesstoken.getnewaccesstoken.getNewAccessToken

internal var accessToken: String? = null

fun refreshToken(
    clientId: String,
    clientSecret: String,
    audience: String,
    getAccessTokenUrl: String,
    okHttpClient: OkHttpClient
): Either<RefreshTokenFailure, String> =
    getNewAccessToken(
        clientId = clientId,
        clientSecret = clientSecret,
        audience = audience,
        getAccessTokenUrl = getAccessTokenUrl,
        okHttpClient = okHttpClient
    ).mapLeft { failure ->
        when (failure) {
            is GetNewAccessTokenFailure.TechnicalException -> RefreshTokenFailure.TechnicalException(failure.throwable)
            GetNewAccessTokenFailure.UnexpectedJsonStructure -> RefreshTokenFailure.UnexpectedJsonStructure
            GetNewAccessTokenFailure.UnsuccessfulResponse -> RefreshTokenFailure.UnsuccessfulResponse
            GetNewAccessTokenFailure.UnexpectedObjectStructure -> RefreshTokenFailure.UnexpectedObjectStructure
        }
    }.flatMap { newToken: String -> newToken.right().also { accessToken = newToken } }

