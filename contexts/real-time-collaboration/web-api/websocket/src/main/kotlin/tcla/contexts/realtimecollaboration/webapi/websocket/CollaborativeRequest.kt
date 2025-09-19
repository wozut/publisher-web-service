package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

abstract class CollaborativeRequest {
    abstract val collaboratorId: UUID
    abstract val collaborativeSessionId: UUID
    abstract val sequenceNumber: Long
}