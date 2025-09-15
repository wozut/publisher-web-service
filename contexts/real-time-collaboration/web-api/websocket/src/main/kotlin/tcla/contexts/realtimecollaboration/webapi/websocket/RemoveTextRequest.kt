package tcla.contexts.realtimecollaboration.webapi.websocket

data class RemoveTextRequest(
    val collaborativeSessionId: String,
    val collaboratorId: String,
    val position: Long,
    val length: Long
)