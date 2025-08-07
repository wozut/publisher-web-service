package tcla.contexts.accounts.auth0.user

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.auth0.json.mgmt.users.User
import jakarta.inject.Named
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import tcla.contexts.accounts.auth0.accesstoken.getcurrentaccesstoken.getCurrentAccessToken
import tcla.contexts.accounts.auth0.accesstoken.refreshtoken.RefreshTokenFailure
import tcla.contexts.accounts.auth0.accesstoken.refreshtoken.refreshToken
import tcla.contexts.accounts.auth0.gettokenanduser.GetTokenAndUserFailure
import tcla.contexts.accounts.auth0.gettokenanduser.getTokenAndUser
import tcla.contexts.accounts.auth0.user.update.UpdateAuth0UserFailure
import tcla.contexts.accounts.auth0.user.update.updateAuth0User
import tcla.contexts.accounts.core.application.failures.FindAccountFailure
import tcla.contexts.accounts.core.domain.account.AccountRepository
import tcla.contexts.accounts.core.domain.account.SaveAccountFailure
import tcla.contexts.accounts.core.domain.account.model.Account
import tcla.contexts.accounts.core.domain.account.model.AccountId

@Named
class Auth0AccountRepository(
    @Value("\${auth0.client-id}") private val clientId: String,
    @Value("\${auth0.client-secret}") private val clientSecret: String,
    @Value("\${auth0.management-api-audience}") private val managementApiAudience: String,
    @Value("\${auth0.get-access-token-url}") private val getAccessTokenUrl: String,
    @Value("\${auth0.domain}") private val domain: String,
    private val okHttpClient: OkHttpClient
) : AccountRepository {
    override fun find(id: AccountId): Either<FindAccountFailure, Account> =
        getTokenAndUser(clientId = clientId, clientSecret = clientSecret, audience = managementApiAudience, getAccessTokenUrl = getAccessTokenUrl, domain = domain, id = id.string, okHttpClient = okHttpClient)
            .fold({ failure ->
                when (failure) {
                    GetTokenAndUserFailure.Auth0UserNotFound -> FindAccountFailure.NotFound.left()
                    GetTokenAndUserFailure.ExpiredToken, GetTokenAndUserFailure.TokenNotSet -> {
                        refreshToken(
                            clientId = clientId,
                            clientSecret = clientSecret,
                            audience = managementApiAudience,
                            getAccessTokenUrl = getAccessTokenUrl,
                            okHttpClient = okHttpClient
                        ).mapLeft {
                            when (it) {
                                is RefreshTokenFailure.UnsuccessfulResponse -> FindAccountFailure.TechnicalException(
                                    RuntimeException("Unable to get new token")
                                )

                                is RefreshTokenFailure.UnexpectedJsonStructure -> FindAccountFailure.TechnicalException(
                                    RuntimeException("Unable to get new token")
                                )

                                is RefreshTokenFailure.TechnicalException -> FindAccountFailure.TechnicalException(
                                    RuntimeException("Unable to get new token")
                                )

                                RefreshTokenFailure.UnexpectedObjectStructure -> FindAccountFailure.TechnicalException(
                                    RuntimeException("Unable to get new token")
                                )
                            }
                        }.flatMap {
                            getTokenAndUser(domain = domain, id = id.string, clientId = clientId, clientSecret = clientSecret, audience = managementApiAudience, getAccessTokenUrl = getAccessTokenUrl, okHttpClient = okHttpClient)
                                .mapLeft { secondFailure: GetTokenAndUserFailure ->
                                    when (secondFailure) {
                                        is GetTokenAndUserFailure.ExpiredToken, is GetTokenAndUserFailure.TokenNotSet -> FindAccountFailure.TechnicalException(
                                            RuntimeException("Unable to get new token")
                                        )

                                        GetTokenAndUserFailure.Auth0UserNotFound -> FindAccountFailure.NotFound
                                        is GetTokenAndUserFailure.TechnicalException -> FindAccountFailure.TechnicalException(
                                            secondFailure.throwable
                                        )

                                        GetTokenAndUserFailure.RefreshToken -> FindAccountFailure.TechnicalException(
                                            RuntimeException("Unable to get new token")
                                        )
                                    }
                                }
                        }

                    }

                    is GetTokenAndUserFailure.TechnicalException -> FindAccountFailure.TechnicalException(failure.throwable)
                        .left()

                    GetTokenAndUserFailure.RefreshToken -> TODO()
                }
            }, { it.right() })

    override fun save(account: Account): Either<SaveAccountFailure, AccountId> =
        getCurrentAccessToken(
            clientId = clientId,
            clientSecret = clientSecret,
            audience = managementApiAudience,
            getAccessTokenUrl = getAccessTokenUrl,
            okHttpClient = okHttpClient
        ).mapLeft { failure -> SaveAccountFailure.GetCurrentAccessToken(RuntimeException(failure.toString())) }
            .flatMap { accessToken ->
                val user = User()
                user.name = account.name.string
                updateAuth0User(domain = domain, id = account.id.string, user = user, accessToken = accessToken)
                    .fold(
                        { updateAuth0UserFailure ->
                            when (updateAuth0UserFailure) {
                                UpdateAuth0UserFailure.Auth0UserNotFound -> TODO()
                                UpdateAuth0UserFailure.ExpiredToken -> updateSecondTry(account, user)
                                is UpdateAuth0UserFailure.TechnicalException -> TODO()
                            }
                        },
                        { account.id.right() }
                    )
            }

    private fun updateSecondTry(
        account: Account,
        user: User
    ): Either<SaveAccountFailure, AccountId> =
        refreshToken(
            clientId = clientId,
            clientSecret = clientSecret,
            audience = managementApiAudience,
            getAccessTokenUrl = getAccessTokenUrl,
            okHttpClient = okHttpClient
        ).mapLeft { _ -> SaveAccountFailure.UnableToGetNewAccessToken }
            .flatMap { newToken ->
                updateAuth0User(domain = domain, id = account.id.string, user = user, accessToken = newToken)
                    .fold(
                        { secondUpdateFailureToSaveFailure(it) },
                        { account.id.right() }
                    )
            }

    private fun secondUpdateFailureToSaveFailure(secondUpdateAuth0UserFailure: UpdateAuth0UserFailure): Either<SaveAccountFailure, Nothing> =
        when (secondUpdateAuth0UserFailure) {
            UpdateAuth0UserFailure.Auth0UserNotFound -> TODO()
            UpdateAuth0UserFailure.ExpiredToken -> SaveAccountFailure.UnableToGetNewAccessToken.left()
            is UpdateAuth0UserFailure.TechnicalException -> TODO()
        }
}
