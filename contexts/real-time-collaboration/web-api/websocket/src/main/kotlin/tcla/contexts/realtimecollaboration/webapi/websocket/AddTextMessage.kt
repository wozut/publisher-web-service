package tcla.contexts.realtimecollaboration.webapi.websocket

data class AddTextMessage(
    val position: Long,
    val text: String,
    override val collaboratorId: String,
    override val collaborativeSessionId: String,
    override val sequenceNumber: Long
): CollaborativeMessage()
