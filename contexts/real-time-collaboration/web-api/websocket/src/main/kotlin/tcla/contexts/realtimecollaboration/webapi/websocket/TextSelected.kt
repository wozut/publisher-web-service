package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class TextSelected(val collaborativeSessionId: UUID, val collaboratorId: UUID, val start: Long, val end: Long)
