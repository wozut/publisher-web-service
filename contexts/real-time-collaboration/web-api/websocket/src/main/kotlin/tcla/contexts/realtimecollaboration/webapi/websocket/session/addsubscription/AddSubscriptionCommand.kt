package tcla.contexts.realtimecollaboration.webapi.websocket.session.addsubscription

import tcla.contexts.realtimecollaboration.webapi.websocket.Subscription
import java.util.UUID

data class AddSubscriptionCommand(
    val requesterId: UUID, val documentId: UUID, val subscription: Subscription
)
