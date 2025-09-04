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
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(jwtAuthenticationInterceptor)
    }
}

@Component
class JwtAuthenticationInterceptor : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        println("preSend Thread name: ${Thread.currentThread().name}")
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (StompCommand.CONNECT == accessor?.command) {
            println("preSend CONNECT")

//            val authorization = accessor.getNativeHeader("Authorization")?.firstOrNull()
            val userId = accessor.getNativeHeader("UserId")?.firstOrNull()

            accessor.sessionAttributes["userId"] = userId

//            if (authorization?.startsWith("Bearer ") == true) {
//                val token = authorization.substring(7)
//                accessor.sessionAttributes["userId"] = token
//                println("preSend Authorization: $token")
//                try {
////                    validateJwtToken(token)
//                } catch (e: Exception) {
//                    throw RuntimeException("Invalid JWT token")
//                }
//            } else {
//                throw RuntimeException("Missing Authorization header")
//            }
        }

        if(StompCommand.SUBSCRIBE == accessor?.command) {
            println("preSend SUBSCRIBE")
            println("sessionAttributes userId ${accessor.sessionAttributes["userId"]}")
        }

        if(StompCommand.SEND == accessor?.command) {
            println("preSend SEND")
            println("sessionAttributes userId ${accessor.sessionAttributes["userId"]}")
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
