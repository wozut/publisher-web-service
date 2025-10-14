package tcla.contexts.realtimecollaboration.webapi.websocket.session.removesubscription

import java.util.UUID

data class RemoveSubscriptionCommand(val requesterId: UUID, val documentId: UUID, val subscriptionId: String)