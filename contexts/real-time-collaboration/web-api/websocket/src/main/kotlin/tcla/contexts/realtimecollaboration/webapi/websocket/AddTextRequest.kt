package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class AddTextRequest(
    val position: Long,
    val text: String,
    override val collaboratorId: UUID,
    override val collaborativeSessionId: UUID,
    override val sequenceNumber: Long
): CollaborativeRequest()
