package tcla.contexts.realtimecollaboration.webapi.websocket.session.leave

import java.util.UUID

data class LeaveSessionCommand(val requesterId: UUID, val documentId: UUID)