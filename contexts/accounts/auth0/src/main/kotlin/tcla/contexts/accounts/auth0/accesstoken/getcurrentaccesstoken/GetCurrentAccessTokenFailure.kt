package tcla.contexts.accounts.auth0.accesstoken.getcurrentaccesstoken

import tcla.contexts.accounts.auth0.accesstoken.refreshtoken.RefreshTokenFailure

sealed class GetCurrentAccessTokenFailure {
    data class RefreshToken(val failure: RefreshTokenFailure) : GetCurrentAccessTokenFailure()
}
