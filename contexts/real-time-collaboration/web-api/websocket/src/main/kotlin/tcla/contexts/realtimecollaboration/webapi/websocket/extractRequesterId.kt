package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.simp.SimpMessageHeaderAccessor

fun extractRequesterId(headerAccessor: SimpMessageHeaderAccessor): String? =
    headerAccessor.sessionAttributes?.get("userId") as? String