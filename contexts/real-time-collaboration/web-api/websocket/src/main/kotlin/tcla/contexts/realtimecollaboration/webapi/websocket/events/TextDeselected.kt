package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextDeselected(
    override val collaborativeSessionId: UUID,
    override val collaboratorId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean
) : CollaborativeEvent() {
    override val type: String = "TextDeselected"
}
