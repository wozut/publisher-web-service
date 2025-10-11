package tcla.contexts.realtimecollaboration.webapi.websocket.session.send

import java.util.UUID

data class SendSessionCommand(
    val requesterId: UUID,
    val documentId: UUID
)
