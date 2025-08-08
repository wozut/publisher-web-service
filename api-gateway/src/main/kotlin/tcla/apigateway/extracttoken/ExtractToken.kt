package tcla.apigateway.extracttoken

import arrow.core.Either
import arrow.core.left
import arrow.core.right


fun extractToken(headerValue: String): Either<ExtractTokenFailure, String> {
    val bearerPrefix = "Bearer "
    if (!headerValue.startsWith(
            prefix = bearerPrefix,
            ignoreCase = false
        )
    ) return ExtractTokenFailure.InvalidValueSyntax.left()

    val token: String = headerValue.removePrefix(bearerPrefix)
    return when {
        token.isEmpty() -> ExtractTokenFailure.InvalidValueSyntax.left()
        else -> token.right()
    }
}
