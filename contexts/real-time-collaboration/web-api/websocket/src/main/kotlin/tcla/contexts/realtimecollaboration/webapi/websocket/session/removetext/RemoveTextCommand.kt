package tcla.contexts.realtimecollaboration.webapi.websocket.session.removetext

import java.util.UUID

data class RemoveTextCommand(
    val requesterId: UUID,
    val sessionId: UUID,
    val writerId: UUID,
    val position: Long,
    val length: Long,
    val sequenceNumber: Long
)