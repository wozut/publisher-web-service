package tcla.contexts.realtimecollaboration.webapi.websocket

data class ChangeCursorPositionRequest(
    val collaborativeSessionId: String,
    val collaboratorId: String,
    val newPosition: Long,
)