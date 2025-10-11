package tcla.contexts.realtimecollaboration.webapi.websocket.session.join

import java.util.UUID

data class JoinSessionCommand(val requesterId: UUID, val documentId: UUID)