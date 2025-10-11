package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.*

data class CursorPositionChanged(
    override val sessionId: UUID,
    override val writerId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean,
    val newPosition: Long
) : SessionEvent() {
    override val type: String = "CursorPositionChanged"
}