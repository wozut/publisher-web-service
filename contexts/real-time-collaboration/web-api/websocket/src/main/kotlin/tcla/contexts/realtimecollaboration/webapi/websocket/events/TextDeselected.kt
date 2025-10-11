package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextDeselected(
    override val sessionId: UUID,
    override val writerId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean
) : SessionEvent() {
    override val type: String = "TextDeselected"
}
