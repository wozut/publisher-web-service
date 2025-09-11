package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextRemoved(val collaborativeSessionId: UUID, val collaboratorId: UUID, val start: Long, val end: Long)
