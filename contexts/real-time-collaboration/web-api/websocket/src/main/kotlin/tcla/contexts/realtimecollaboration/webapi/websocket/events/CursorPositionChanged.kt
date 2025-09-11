package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class CursorPositionChanged(val collaborativeSessionId: UUID, val collaboratorId: UUID, val newPosition: Long)