package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition

import java.util.UUID

data class ChangeCursorPositionCommand(val requesterId: UUID, val collaborativeSessionId: UUID, val collaboratorId: UUID, val newPosition: Long)
