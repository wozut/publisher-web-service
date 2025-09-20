package tcla.contexts.realtimecollaboration.webapi.websocket.requests

import java.util.UUID

data class DeselectTextRequest(
    override val collaborativeSessionId: UUID,
    override val collaboratorId: UUID,
    override val sequenceNumber: Long,
    override var status: Status = Status.PENDING
): CollaborativeRequest()
