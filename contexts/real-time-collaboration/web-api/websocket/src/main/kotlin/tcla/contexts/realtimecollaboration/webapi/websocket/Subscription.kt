package tcla.contexts.realtimecollaboration.webapi.websocket

data class Subscription(
    val id: String,
    val type: Type
) {
    enum class Type {
        UPDATES,
        SESSION
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subscription

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
