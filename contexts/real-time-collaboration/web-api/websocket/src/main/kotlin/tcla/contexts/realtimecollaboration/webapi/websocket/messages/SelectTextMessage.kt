package tcla.contexts.realtimecollaboration.webapi.websocket.messages

data class SelectTextMessage(
    val position: Long,
    val length: Long,
    override val sessionId: String,
    override val writerId: String,
    override val sequenceNumber: Long
): SessionMessage()