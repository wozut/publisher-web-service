package tcla.contexts.realtimecollaboration.webapi.websocket.session.send

import tcla.contexts.realtimecollaboration.webapi.websocket.Subscription
import java.util.UUID

data class SendSessionCommand(
    val requesterId: UUID,
    val documentId: UUID,
    val subscription: Subscription
)
