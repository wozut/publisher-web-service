package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class WriterLeft(
    override val writerId: UUID,
    override val sessionId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean
) : SessionEvent() {
    override val type: String = "CollaboratorLeft"
}