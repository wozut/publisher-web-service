package tcla.contexts.realtimecollaboration.webapi.websocket.session.changecursorposition

import java.util.UUID

data class ChangeCursorPositionCommand(
    val requesterId: UUID,
    val sessionId: UUID,
    val writerId: UUID,
    val newPosition: Long,
    val sequenceNumber: Long
)
