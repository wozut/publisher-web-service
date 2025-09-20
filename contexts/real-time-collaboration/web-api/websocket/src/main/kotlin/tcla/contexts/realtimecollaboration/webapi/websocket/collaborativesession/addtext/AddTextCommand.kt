package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext

import java.util.UUID

data class AddTextCommand(
    val requesterId: UUID,
    val collaborativeSessionId: UUID,
    val collaboratorId: UUID,
    val position: Long,
    val text: String,
    val sequenceNumber: Long
)