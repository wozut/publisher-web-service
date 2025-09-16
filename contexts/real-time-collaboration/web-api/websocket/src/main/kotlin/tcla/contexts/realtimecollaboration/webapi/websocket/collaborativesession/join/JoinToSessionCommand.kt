package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.join

import java.util.UUID

data class JoinToSessionCommand(val requesterId: UUID, val documentId: UUID)