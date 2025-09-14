package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition

data class ChangeCursorPositionCommand(val collaborativeSessionId: UUID, val newPosition: Long)
