package tcla.contexts.realtimecollaboration.webapi.websocket.session.join

import java.util.*

data class JoinSessionCommand(val requesterId: UUID, val documentId: UUID)