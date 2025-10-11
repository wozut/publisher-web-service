package tcla.contexts.realtimecollaboration.webapi.websocket.requests

import java.util.UUID

sealed class SessionRequest {
    abstract val writerId: UUID
    abstract val sessionId: UUID
    abstract val sequenceNumber: Long
    abstract var status: Status

    enum class Status {
        PENDING, PROCESSING, PROCESSED
    }
    fun markAsProcessing() {
        if(status != Status.PENDING) throw IllegalStateException()
        status = Status.PROCESSING
    }
    fun markAsProcessed() {
        if(status != Status.PROCESSING) throw IllegalStateException()
        status = Status.PROCESSED
    }
}
