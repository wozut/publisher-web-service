package tcla.contexts.realtimecollaboration.webapi.websocket.messages

data class RemoveTextMessage(
    val position: Long,
    val length: Long,
    override val collaborativeSessionId: String,
    override val collaboratorId: String,
    override val sequenceNumber: Long
): CollaborativeMessage()