package tcla.contexts.realtimecollaboration.webapi.websocket

import tcla.contexts.realtimecollaboration.webapi.websocket.messages.CollaborativeMessage

data class ChangeCursorPositionMessage(
    val newPosition: Long,
    override val collaborativeSessionId: String,
    override val collaboratorId: String,
    override val sequenceNumber: Long
): CollaborativeMessage()