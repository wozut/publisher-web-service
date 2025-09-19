package tcla.contexts.realtimecollaboration.webapi.websocket

data class ChangeCursorPositionMessage(
    val newPosition: Long,
    override val collaborativeSessionId: String,
    override val collaboratorId: String,
    override val sequenceNumber: Long
): CollaborativeMessage()