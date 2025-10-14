package tcla.contexts.realtimecollaboration.webapi.websocket.session.deselecttext

import java.util.UUID

data class DeselectTextCommand(
    val requesterId: UUID,
    val sessionId: UUID,
    val writerId: UUID,
    val sequenceNumber: Long
)