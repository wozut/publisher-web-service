package tcla.contexts.realtimecollaboration.webapi.websocket

data class Subscription(
    val id: String,
    val type: Type
) {
    enum class Type {
        UPDATES,
        SESSION
    }
}
