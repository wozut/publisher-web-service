package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

abstract class CollaborativeEvent {
    abstract val collaboratorId: UUID
    abstract val collaborativeSessionId: UUID
    abstract val sequenceNumber: Long
    abstract val type: String
    abstract var broadcasted: Boolean

    fun markAsBroadcasted() = apply { broadcasted = true }
}