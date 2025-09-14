package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextAdded(
    override val collaborativeSessionId: UUID,
    override val collaboratorId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean,
    val position: Long,
    val text: String
) : CollaborativeEvent() {
    override val type: String = "TextAdded"
}
