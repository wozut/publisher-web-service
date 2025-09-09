package tcla.contexts.realtimecollaboration.webapi.websocket

data class Document(
    val id: String,
    var content: String,
    val collaborators: MutableSet<String> = mutableSetOf(),
    var version: Long = 0L
)