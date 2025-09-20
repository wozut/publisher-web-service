package tcla.contexts.realtimecollaboration.webapi.websocket.requests

import java.util.UUID

data class AddTextRequest(
    val position: Long,
    val text: String,
    override val collaboratorId: UUID,
    override val collaborativeSessionId: UUID,
    override val sequenceNumber: Long,
    override var status: Status = Status.PENDING
): CollaborativeRequest()