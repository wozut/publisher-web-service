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
import org.springframework.stereotype.Component
import tcla.contexts.authentication.core.RequestInfo


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
//            .setAllowedOriginPatterns("*")
            .setAllowedOrigins("*")
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
            println("user id: ${RequestInfo.getRequesterId()}")
            val authorization = accessor.getNativeHeader("Authorization")?.firstOrNull()

            if (authorization?.startsWith("Bearer ") == true) {
                val token = authorization.substring(7)
                try {
                    validateJwtToken(token)
                } catch (e: Exception) {
                    throw RuntimeException("Invalid JWT token")
                }
            } else {
                throw RuntimeException("Missing Authorization header")
            }
        }
        
        return message
    }
    
    private fun validateJwtToken(token: String) {
        // TODO: Implement JWT validation logic
        if (token.isBlank()) {
            throw IllegalArgumentException("Token is blank")
        }
    }
}
