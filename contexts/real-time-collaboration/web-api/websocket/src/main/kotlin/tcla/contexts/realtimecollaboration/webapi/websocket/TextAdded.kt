package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class TextAdded(val collaborativeSessionId: UUID, val collaboratorId: UUID, val position: Long, val text: String)
