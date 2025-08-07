package tcla.contexts.accounts.auth0.accesstoken.getcurrentaccesstoken

import arrow.core.Either
import arrow.core.right
import okhttp3.OkHttpClient
import tcla.contexts.accounts.auth0.accesstoken.refreshtoken.RefreshTokenFailure
import tcla.contexts.accounts.auth0.accesstoken.refreshtoken.accessToken
import tcla.contexts.accounts.auth0.accesstoken.refreshtoken.refreshToken

fun getCurrentAccessToken(
    clientId: String,
    clientSecret: String,
    audience: String,
    getAccessTokenUrl: String,
    okHttpClient: OkHttpClient
): Either<GetCurrentAccessTokenFailure, String> =
    when (val token = accessToken) {
        null -> refreshToken(
            clientId = clientId,
            clientSecret = clientSecret,
            audience = audience,
            getAccessTokenUrl = getAccessTokenUrl,
            okHttpClient = okHttpClient
        ).mapLeft { refreshTokenFailure -> refreshTokenFailure.toFailure() }

        else -> token.right()
    }

private fun RefreshTokenFailure.toFailure(): GetCurrentAccessTokenFailure =
    when (this) {
        is RefreshTokenFailure.TechnicalException, is RefreshTokenFailure.UnexpectedJsonStructure, is RefreshTokenFailure.UnexpectedObjectStructure, is RefreshTokenFailure.UnsuccessfulResponse ->
            GetCurrentAccessTokenFailure.RefreshToken(this)
    }
