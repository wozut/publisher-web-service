package tcla.contexts.realtimecollaboration.webapi.websocket.messages

data class AddTextMessage(
    val position: Long,
    val text: String,
    override val writerId: String,
    override val sessionId: String,
    override val sequenceNumber: Long
): SessionMessage()