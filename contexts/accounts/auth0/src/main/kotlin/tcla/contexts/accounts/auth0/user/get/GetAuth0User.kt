package tcla.contexts.accounts.auth0.user.get

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.client.mgmt.filter.UserFilter
import com.auth0.exception.APIException
import com.auth0.exception.Auth0Exception
import com.auth0.json.mgmt.users.User
import com.auth0.net.Request
import com.auth0.net.Response
import tcla.contexts.accounts.auth0.user.Auth0UserFields

fun getAuth0User(domain: String, id: String, accessToken: String): Either<GetAuth0UserFailure, User> =
    ManagementAPI
        .newBuilder(domain, accessToken)
        .build()
        .users()
        .get(id, UserFilter().withFields(Auth0UserFields.value.joinToString(separator = ","), true))
        .let { request: Request<User> -> Either.catch { request.execute() }
            .mapLeft { failure: Throwable ->
                when (failure) {
                    is APIException -> when (failure.statusCode) {
                        401 -> when (failure.getValue("message")) {
                            "Expired token received for JSON Web Token validation" -> GetAuth0UserFailure.ExpiredToken
                            else -> GetAuth0UserFailure.TechnicalException(failure)
                        }

                        404 -> GetAuth0UserFailure.Auth0UserNotFound
                        else -> TODO()
                    }

                    is Auth0Exception -> GetAuth0UserFailure.TechnicalException(failure)

                    else -> TODO("Branch for future supported failures")
                }
            }
        }
        .flatMap { response: Response<User> -> response.body.right() }

