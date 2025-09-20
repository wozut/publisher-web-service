package tcla.contexts.realtimecollaboration.webapi.websocket.requests

data class DeselectTextRequest(
    val collaborativeSessionId: String,
    val collaboratorId: String
)
