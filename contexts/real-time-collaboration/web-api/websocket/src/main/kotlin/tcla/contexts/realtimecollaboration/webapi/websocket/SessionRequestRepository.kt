package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SessionRequest
import java.util.UUID

private val sessionRequests: MutableList<SessionRequest> = mutableListOf()

@Repository
class SessionRequestRepository {

    @Synchronized
    fun create(sessionRequest: SessionRequest) {
        if (sessionRequests.any { areSame(it, sessionRequest) }) throw IllegalArgumentException(
            "SessionRequest already exists"
        )

        if (!sessionRequests.add(sessionRequest)) throw IllegalStateException("Failed to create SessionRequest")
    }

    private fun areSame(
        request: SessionRequest,
        sessionRequest: SessionRequest
    ): Boolean = request.sequenceNumber == sessionRequest.sequenceNumber &&
            request.sessionId == sessionRequest.sessionId &&
            request.writerId == sessionRequest.writerId

    fun findOldestByStatus(status: SessionRequest.Status): SessionRequest? = sessionRequests
        .filter { it.status == status }
        .minByOrNull { it.sequenceNumber }

    @Synchronized
    fun saveChanges(sessionRequest: SessionRequest): SessionRequest {
        if (sessionRequests.none { areSame(it, sessionRequest) }) throw IllegalArgumentException(
            "SessionRequest not found"
        )

        sessionRequests.removeIf { areSame(it, sessionRequest) }
        if (!sessionRequests.add(sessionRequest)) throw IllegalStateException("Failed to save changes")
        return sessionRequest
    }

    fun existsBySessionAndWriterAndSequenceNumberAndStatus(
        sessionId: UUID,
        writerId: UUID,
        sequenceNumber: Long,
        status: SessionRequest.Status
    ): Boolean {
        return sessionRequests.any { request ->
            request.sessionId == sessionId &&
                    request.writerId == writerId &&
                    request.sequenceNumber == sequenceNumber &&
                    request.status == status
        }
    }
}