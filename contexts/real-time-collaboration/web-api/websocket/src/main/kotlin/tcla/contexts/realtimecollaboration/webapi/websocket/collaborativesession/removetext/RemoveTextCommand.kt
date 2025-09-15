package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext

import java.util.UUID

data class RemoveTextCommand(
    val requesterId: UUID,
    val collaborativeSessionId: UUID,
    val collaboratorId: UUID,
    val position: Long,
    val length: Long
)