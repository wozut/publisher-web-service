package tcla.contexts.realtimecollaboration.webapi.websocket.session.join

import tcla.contexts.realtimecollaboration.webapi.websocket.Subscription
import java.util.UUID

data class JoinSessionCommand(val requesterId: UUID, val documentId: UUID, val subscription: Subscription)