package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class TextAdded(val collaborationSessionId: UUID, val collaboratorId: UUID, val position: Long, val text: String)
