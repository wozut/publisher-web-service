package tcla.contexts.realtimecollaboration.webapi.websocket

data class DocumentChange(
    val documentId: String,
    val content: String,
    val userId: String,
    val version: Long,
    val timestamp: Long = System.currentTimeMillis()
)