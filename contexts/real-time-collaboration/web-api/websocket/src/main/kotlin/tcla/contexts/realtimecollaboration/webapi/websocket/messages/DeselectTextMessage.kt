package tcla.contexts.realtimecollaboration.webapi.websocket.messages

data class DeselectTextMessage(
    override val collaborativeSessionId: String,
    override val collaboratorId: String,
    override val sequenceNumber: Long
): CollaborativeMessage()