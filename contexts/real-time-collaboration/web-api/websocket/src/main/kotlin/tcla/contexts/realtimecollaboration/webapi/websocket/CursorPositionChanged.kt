package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class CursorPositionChanged(val collaborationSessionId: UUID, val collaboratorId: UUID, val newPosition: Long)