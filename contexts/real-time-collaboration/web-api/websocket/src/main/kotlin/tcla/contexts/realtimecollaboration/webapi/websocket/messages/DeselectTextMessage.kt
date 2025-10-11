package tcla.contexts.realtimecollaboration.webapi.websocket.messages

data class DeselectTextMessage(
    override val sessionId: String,
    override val writerId: String,
    override val sequenceNumber: Long
): SessionMessage()