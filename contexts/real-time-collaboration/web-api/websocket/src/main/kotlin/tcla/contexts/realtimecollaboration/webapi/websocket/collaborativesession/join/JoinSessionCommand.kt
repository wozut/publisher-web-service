package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.join

import java.util.UUID

data class JoinSessionCommand(val requesterId: UUID, val documentId: UUID)