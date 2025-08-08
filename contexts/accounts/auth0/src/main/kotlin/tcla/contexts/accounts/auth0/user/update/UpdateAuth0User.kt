package tcla.contexts.accounts.auth0.user.update

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.exception.APIException
import com.auth0.exception.Auth0Exception
import com.auth0.json.mgmt.users.User
import com.auth0.net.Request
import com.auth0.net.Response

fun updateAuth0User(
    domain: String,
    id: String,
    user: User,
    accessToken: String
): Either<UpdateAuth0UserFailure, Unit> =
    ManagementAPI
        .newBuilder(domain, accessToken)
        .build()
        .users()
        .update(id, user)
        .let { request: Request<User> ->
            Either.catch { request.execute() }
                .mapLeft { failure: Throwable ->
                    when (failure) {
                        is APIException -> when (failure.statusCode) {
                            401 -> when (failure.getValue("message")) {
                                "Expired token received for JSON Web Token validation" -> UpdateAuth0UserFailure.ExpiredToken
                                else -> UpdateAuth0UserFailure.TechnicalException(failure)
                            }

                            404 -> UpdateAuth0UserFailure.Auth0UserNotFound
                            else -> TODO()
                        }

                        is Auth0Exception -> UpdateAuth0UserFailure.TechnicalException(failure)

                        else -> TODO("Branch for future supported failures")
                    }
                }
        }
        .flatMap { _: Response<User> -> Unit.right() }

