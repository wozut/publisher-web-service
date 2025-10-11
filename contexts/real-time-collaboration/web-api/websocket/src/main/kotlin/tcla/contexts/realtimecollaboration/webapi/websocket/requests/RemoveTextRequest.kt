package tcla.contexts.realtimecollaboration.webapi.websocket.requests

import java.util.UUID

data class RemoveTextRequest(
    val position: Long,
    val length: Long,
    override val sessionId: UUID,
    override val writerId: UUID,
    override val sequenceNumber: Long,
    override var status: Status = Status.PENDING
): SessionRequest()