package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext

import java.util.UUID

data class SelectTextCommand(
    val requesterId: UUID,
    val collaborativeSessionId: UUID,
    val collaboratorId: UUID,
    val position: Long,
    val length: Long,
    val sequenceNumber: Long
)