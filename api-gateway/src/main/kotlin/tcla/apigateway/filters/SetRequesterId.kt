package tcla.apigateway.filters

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import tcla.contexts.authentication.core.RequestInfo


/**
 * Each context should have a filter like this one, but it is placed only here because API Gateway and all contexts
 * are in the same application and there is no easy way to execute only some filters depending on the
 * context of the targeted endpoint. Once we extract microservices, this must be taken in account.
 */
@Component
@Order(3)
class SetRequesterId: Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpServletRequest = request as HttpServletRequest
        RequestInfo.setRequesterId(httpServletRequest.getHeader("Account-Id"))
        chain.doFilter(httpServletRequest, response)
    }
}
