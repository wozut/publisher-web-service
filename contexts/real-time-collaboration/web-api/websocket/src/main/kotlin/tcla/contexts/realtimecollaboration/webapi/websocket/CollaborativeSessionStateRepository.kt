package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import java.util.UUID

private val collaborativeSessionStates: MutableList<CollaborativeSessionState> = mutableListOf()

@Repository
class CollaborativeSessionStateRepository {
    fun findByDocumentId(documentId: UUID): CollaborativeSessionState =
        collaborativeSessionStates.first { it.documentState.documentId == documentId }

    fun saveChanges(collaborativeSessionState: CollaborativeSessionState): CollaborativeSessionState {
        if(collaborativeSessionStates.none { it.id == collaborativeSessionState.id }) throw IllegalArgumentException("CollaborativeSessionState not found")

        collaborativeSessionStates.removeIf { it.id == collaborativeSessionState.id }
        if(!collaborativeSessionStates.add(collaborativeSessionState)) throw IllegalStateException("Failed to save changes")
        return collaborativeSessionState
    }

    fun existsByDocumentId(documentId: UUID): Boolean {
        return collaborativeSessionStates.any { it.documentState.documentId == documentId }
    }

    fun create(collaborativeSessionState: CollaborativeSessionState) {
        if(collaborativeSessionStates.any { it.id == collaborativeSessionState.id }) throw IllegalArgumentException("CollaborativeSessionState already exists")
        if(!collaborativeSessionStates.add(collaborativeSessionState)) throw IllegalStateException("Failed to create CollaborativeSessionState")
    }
}