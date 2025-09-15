package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext

import java.util.UUID

data class DeselectTextCommand(
    val requesterId: UUID,
    val collaborativeSessionId: UUID,
    val collaboratorId: UUID
)