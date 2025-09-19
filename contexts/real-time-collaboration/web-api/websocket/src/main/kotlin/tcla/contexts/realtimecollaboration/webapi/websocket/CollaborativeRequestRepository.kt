package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import java.util.UUID

private val collaborativeRequests: MutableList<CollaborativeRequest> = mutableListOf()

@Repository
class CollaborativeRequestRepository {

    fun create(collaborativeRequest: CollaborativeRequest) {
        if (collaborativeRequests.any { areSame(it, collaborativeRequest) }) throw IllegalArgumentException(
            "CollaborativeRequest already exists"
        )

        if (!collaborativeRequests.add(collaborativeRequest)) throw IllegalStateException("Failed to create CollaborativeRequest")
    }

    private fun areSame(
        request: CollaborativeRequest,
        collaborativeRequest: CollaborativeRequest
    ): Boolean = request.sequenceNumber == collaborativeRequest.sequenceNumber &&
            request.collaborativeSessionId == collaborativeRequest.collaborativeSessionId &&
            request.collaboratorId == collaborativeRequest.collaboratorId

    fun findOldestByStatus(status: CollaborativeRequest.Status): CollaborativeRequest? = collaborativeRequests
        .filter { it.status == status }
        .minByOrNull { it.sequenceNumber }

    fun saveChanges(collaborativeRequest: CollaborativeRequest): CollaborativeRequest {
        if (collaborativeRequests.none { areSame(it, collaborativeRequest) }) throw IllegalArgumentException(
            "CollaborativeRequest not found"
        )

        collaborativeRequests.removeIf { areSame(it, collaborativeRequest) }
        if (!collaborativeRequests.add(collaborativeRequest)) throw IllegalStateException("Failed to save changes")
        return collaborativeRequest
    }

    fun existsByCollaborativeSessionAndCollaboratorAndSequenceNumberAndStatus(
        collaborativeSessionId: UUID,
        collaboratorId: UUID,
        sequenceNumber: Long,
        status: CollaborativeRequest.Status
    ): Boolean {
        return collaborativeRequests.any { request ->
            request.collaborativeSessionId == collaborativeSessionId &&
                    request.collaboratorId == collaboratorId &&
                    request.sequenceNumber == sequenceNumber &&
                    request.status == status
        }
    }
}