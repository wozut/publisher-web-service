package tcla.contexts.realtimecollaboration.webapi.websocket

data class DeselectTextRequest(
    val collaborativeSessionId: String,
    val collaboratorId: String
)
