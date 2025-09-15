package tcla.contexts.realtimecollaboration.webapi.websocket

data class SelectTextRequest(
    val collaborativeSessionId: String,
    val collaboratorId: String,
    val position: Long,
    val length: Long
)
