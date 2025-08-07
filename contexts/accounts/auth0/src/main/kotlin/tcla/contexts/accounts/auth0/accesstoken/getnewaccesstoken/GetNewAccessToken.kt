package tcla.contexts.accounts.auth0.accesstoken.getnewaccesstoken

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import tcla.contexts.accounts.auth0.accesstoken.OauthTokenResource
import tcla.libraries.jsonserialization.deserialization.DeserializeJsonFailure
import tcla.libraries.jsonserialization.deserialization.deserializeJsonAs

fun getNewAccessToken(
    clientId: String,
    clientSecret: String,
    audience: String,
    getAccessTokenUrl: String,
    okHttpClient: OkHttpClient
): Either<GetNewAccessTokenFailure, String> {
    val postBody = """
            {
                "client_id": "$clientId",
                "client_secret": "$clientSecret",
                "audience": "$audience",
                "grant_type": "client_credentials"
            }
        """.trimIndent()

    val request = Request.Builder()
        .url(getAccessTokenUrl)
        .post(postBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .build()

    return okHttpClient.newCall(request)
        .let { Either.catch { it.execute() }.mapLeft { failure -> toGetNewAccessTokenFailure(failure) } }
        .flatMap { response ->
            response.use {
                if (response.code != 200) GetNewAccessTokenFailure.UnsuccessfulResponse.left()
                else response.body!!.string().right()
            }
        }.flatMap { responseBody: String ->
            responseBody.deserializeJsonAs(OauthTokenResource::class)
                .mapLeft { failure: DeserializeJsonFailure ->
                    when (failure) {
                        DeserializeJsonFailure.BuildAdapter -> GetNewAccessTokenFailure.UnexpectedObjectStructure
                        DeserializeJsonFailure.JsonStructureNotMatchObject -> GetNewAccessTokenFailure.UnexpectedJsonStructure
                    }
                }
        }.flatMap { oauthTokenResponse -> oauthTokenResponse.access_token.right() }
}

private fun toGetNewAccessTokenFailure(failure: Throwable) = GetNewAccessTokenFailure.TechnicalException(failure)
