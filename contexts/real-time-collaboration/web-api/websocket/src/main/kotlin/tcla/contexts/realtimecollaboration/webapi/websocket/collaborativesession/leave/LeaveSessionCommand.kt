package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.leave

import java.util.UUID

data class LeaveSessionCommand(val requesterId: UUID, val documentId: UUID)