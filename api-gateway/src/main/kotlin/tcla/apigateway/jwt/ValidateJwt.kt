package tcla.apigateway.jwt

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.auth0.jwk.Jwk
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey

fun validateJwt(token: String, domain: String, audience: String): Either<ValidateJwtFailure, DecodedJWT> =
    Either.catch { verifyAndDecodeJwt(token, domain) }
        .mapLeft { ValidateJwtFailure.Invalid(it) }
        .flatMap { decodedJwt: DecodedJWT ->
            when {
                decodedJwt.audience == null || decodedJwt.audience.none { it == audience } -> ValidateJwtFailure.InvalidAudience.left()
                else -> decodedJwt.right()
            }
        }

private fun verifyAndDecodeJwt(token: String, domain: String): DecodedJWT {
    val urlJwkProvider = UrlJwkProvider(domain)
    val jwt: DecodedJWT = JWT.decode(token)
    val jwk: Jwk = urlJwkProvider.get(jwt.keyId)
    val publicKey: PublicKey = jwk.publicKey
    val algorithm: Algorithm = Algorithm.RSA256(publicKey as RSAPublicKey, null)
    val jwtVerifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer("$domain/")
        .build()
    return jwtVerifier.verify(jwt)
}
