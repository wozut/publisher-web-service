package tcla.contexts.realtimecollaboration.webapi.websocket.requests

import java.util.UUID

data class AddTextRequest(
    val position: Long,
    val text: String,
    override val writerId: UUID,
    override val sessionId: UUID,
    override val sequenceNumber: Long,
    override var status: Status = Status.PENDING
): SessionRequest()