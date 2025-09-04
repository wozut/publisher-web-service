package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfiguration(
    private val jwtAuthenticationInterceptor: JwtAuthenticationInterceptor
) : WebSocketMessageBrokerConfigurer {
    
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic", "/queue")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/collaborative-editor")
            .setAllowedOriginPatterns("*")
    }
    
    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(jwtAuthenticationInterceptor)
    }
}

@Component
class JwtAuthenticationInterceptor : ChannelInterceptor {
    
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
        
        if (StompCommand.CONNECT == accessor?.command) {
            val authorization = accessor.getNativeHeader("Authorization")?.firstOrNull()

            if (authorization?.startsWith("Bearer ") == true) {
                val token = authorization.substring(7)
                try {
                    val authentication = validateJwtToken(token)
                    accessor.user = authentication
                    SecurityContextHolder.getContext().authentication = authentication
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid JWT token", e)
                }
            } else {
                throw IllegalArgumentException("Missing Authorization header")
            }
        }
        
        return message
    }
    
    private fun validateJwtToken(token: String): Authentication {
        return JwtAuthentication(token)
    }
}

class JwtAuthentication(private val token: String) : Authentication {
    override fun getName(): String = token
    override fun getAuthorities() = emptyList<Nothing>()
    override fun getCredentials() = token
    override fun getDetails() = null
    override fun getPrincipal() = token
    override fun isAuthenticated() = true
    override fun setAuthenticated(isAuthenticated: Boolean) {}
}