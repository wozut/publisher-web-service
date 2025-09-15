package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextSelected(
    override val collaborativeSessionId: UUID,
    override val collaboratorId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean,
    val position: Long,
    val length: Long,
): CollaborativeEvent() {
    override val type: String = "TextSelected"
}
