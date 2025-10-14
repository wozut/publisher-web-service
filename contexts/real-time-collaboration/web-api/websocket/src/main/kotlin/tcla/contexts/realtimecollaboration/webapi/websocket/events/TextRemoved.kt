package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextRemoved(
    override val sessionId: UUID,
    override val writerId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean,
    val position: Long,
    val length: Long
) : SessionEvent() {
    override val type: String = "TextRemoved"
}
