package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class CollaboratorLeft(
    override val collaboratorId: UUID,
    override val collaborativeSessionId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean
) : CollaborativeEvent() {
    override val type: String = "CollaboratorLeft"
}