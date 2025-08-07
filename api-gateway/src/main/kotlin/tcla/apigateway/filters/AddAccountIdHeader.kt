package tcla.apigateway.filters

import arrow.core.flatMap
import arrow.core.right
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import tcla.apigateway.extracttoken.ExtractTokenFailure
import tcla.apigateway.extracttoken.extractToken
import tcla.apigateway.jwt.ValidateJwtFailure
import tcla.apigateway.jwt.validateJwt


@Component
@Order(2)
class AddAccountIdHeader(
    @Value("\${auth0.domain-with-scheme}") private val auth0DomainWithScheme: String,
    @Value("\${auth0.audience}") private val auth0Audience: String
) : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val mutableRequest = MutableHttpServletRequestWrapper(request as HttpServletRequest)

        val headerValue: String = mutableRequest.getHeader("Authorization")
            ?: return chain.doFilter(mutableRequest, response)

        extractToken(headerValue)
            .mapLeft { extractTokenFailure ->
                when (extractTokenFailure) {
                    ExtractTokenFailure.InvalidValueSyntax -> return chain.doFilter(mutableRequest, response)
                }
            }.flatMap { token: String ->
                validateJwt(token, auth0DomainWithScheme, auth0Audience)
                    .mapLeft { validateJwtFailure ->
                        when (validateJwtFailure) {
                            is ValidateJwtFailure.Invalid -> return chain.doFilter(mutableRequest, response)
                            ValidateJwtFailure.InvalidAudience -> return chain.doFilter(mutableRequest, response)
                        }
                    }
            }.flatMap { decodedJwt: DecodedJWT ->
                if (decodedJwt.subject != null) mutableRequest.putHeader("Account-Id", decodedJwt.subject)
                Unit.right()
            }
        chain.doFilter(mutableRequest, response)
    }
}
