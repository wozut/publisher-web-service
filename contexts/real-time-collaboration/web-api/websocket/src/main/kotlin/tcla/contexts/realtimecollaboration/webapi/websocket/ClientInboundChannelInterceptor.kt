package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID.fromString

@Component
class ClientInboundChannelInterceptor : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        println("inbound preSend Thread name: ${Thread.currentThread().name}")
        val accessor: StompHeaderAccessor? = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (StompCommand.CONNECT == accessor?.command) {
            println("inbound preSend CONNECT")

            // TODO: validate authentication here
//            val authorization = accessor.getNativeHeader("Authorization")?.firstOrNull()
            val userId: String? = accessor.getNativeHeader("UserId")?.firstOrNull()
            println("inbound preSend getNativeHeader(\"UserId\"): $userId")
            accessor.sessionAttributes["userId"] = userId
            println("inbound preSend sessionAttributes userId ${accessor.sessionAttributes?.get("userId")}")

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

        val destination: String? = accessor?.destination

        if(StompCommand.SUBSCRIBE == accessor?.command) {
            println("inbound preSend SUBSCRIBE")
            println("sessionAttributes userId ${accessor.sessionAttributes["userId"]}")


            val topicUpdatesPrefix = "/topic/updates/"
            if(destination != null && destination.startsWith(topicUpdatesPrefix)) {
                val documentId = destination.removePrefix(topicUpdatesPrefix)
                accessor.sessionAttributes["documentId"] = documentId
            }
        }

        if(StompCommand.SEND == accessor?.command) {
            println("inbound preSend SEND. Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. Destination ${accessor.destination}")
//            println("sessionAttributes userId ${accessor.sessionAttributes["userId"]}")
        }

        if(StompCommand.UNSUBSCRIBE == accessor?.command) {
            println("inbound preSend UNSUBSCRIBE")
            val userId = accessor.sessionAttributes?.get("userId") as? String
            println("User $userId unsubscribed from subscription: ${accessor.subscriptionId}")
            // Ejecutar lógica cuando se desuscribe
        }

        if(StompCommand.DISCONNECT == accessor?.command) {
            println("inbound preSend DISCONNECT")
            val userId = accessor.sessionAttributes?.get("userId") as? String
            println("User $userId disconnected")
            // Ejecutar lógica cuando se desconecta
        }

        println("inbound preSend getNativeHeader(\"destination\"): $destination")

        return message
    }
}