package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.*

data class CursorPositionChanged(
    override val collaborativeSessionId: UUID,
    override val collaboratorId: UUID,
    override val sequenceNumber: Long,
    override var broadcasted: Boolean,
    val newPosition: Long
) : CollaborativeEvent() {
    override val type: String = "CursorPositionChanged"
}