package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

sealed class SessionEvent {
    abstract val writerId: UUID
    abstract val sessionId: UUID
    abstract val sequenceNumber: Long
    abstract val type: String
    abstract var broadcasted: Boolean

    fun markAsBroadcasted() = apply { broadcasted = true }
}