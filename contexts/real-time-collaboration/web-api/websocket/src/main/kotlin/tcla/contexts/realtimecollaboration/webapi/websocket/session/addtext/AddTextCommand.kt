package tcla.contexts.realtimecollaboration.webapi.websocket.session.addtext

import java.util.UUID

data class AddTextCommand(
    val requesterId: UUID,
    val sessionId: UUID,
    val writerId: UUID,
    val position: Long,
    val text: String,
    val sequenceNumber: Long
)