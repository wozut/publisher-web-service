package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextAdded(
    override val sessionId: UUID,
    override val writerId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean,
    val position: Long,
    val text: String
) : SessionEvent() {
    override val type: String = "TextAdded"
}
