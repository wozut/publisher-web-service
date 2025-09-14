package tcla.contexts.realtimecollaboration.webapi.websocket

data class AddTextRequest(
    val collaborativeSessionId: String,
    val collaboratorId: String,
    val position: Long,
    val text: String
)
