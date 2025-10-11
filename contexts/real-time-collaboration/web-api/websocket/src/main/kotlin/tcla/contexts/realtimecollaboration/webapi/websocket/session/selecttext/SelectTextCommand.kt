package tcla.contexts.realtimecollaboration.webapi.websocket.session.selecttext

import java.util.UUID

data class SelectTextCommand(
    val requesterId: UUID,
    val sessionId: UUID,
    val writerId: UUID,
    val position: Long,
    val length: Long,
    val sequenceNumber: Long
)