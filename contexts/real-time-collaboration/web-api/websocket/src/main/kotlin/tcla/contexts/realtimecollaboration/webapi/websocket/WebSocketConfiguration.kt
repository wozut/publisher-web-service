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
    private val clientInboundChannelInterceptor: ClientInboundChannelInterceptor
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/collaborative-session")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(clientInboundChannelInterceptor)
    }
}

@Component
class ClientInboundChannelInterceptor : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        println("preSend Thread name: ${Thread.currentThread().name}")
        val accessor: StompHeaderAccessor? = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (StompCommand.CONNECT == accessor?.command) {
            println("preSend CONNECT")

            // TODO: validate authentication here
//            val authorization = accessor.getNativeHeader("Authorization")?.firstOrNull()
            val userId: String? = accessor.getNativeHeader("UserId")?.firstOrNull()

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

        if(StompCommand.UNSUBSCRIBE == accessor?.command) {
            println("preSend UNSUBSCRIBE")
            val userId = accessor.sessionAttributes?.get("userId") as? String
            println("User $userId unsubscribed from subscription: ${accessor.subscriptionId}")
            // Ejecutar lógica cuando se desuscribe
        }

        if(StompCommand.DISCONNECT == accessor?.command) {
            println("preSend DISCONNECT")  
            val userId = accessor.sessionAttributes?.get("userId") as? String
            println("User $userId disconnected")
            // Ejecutar lógica cuando se desconecta
        }

        return message
    }
}
