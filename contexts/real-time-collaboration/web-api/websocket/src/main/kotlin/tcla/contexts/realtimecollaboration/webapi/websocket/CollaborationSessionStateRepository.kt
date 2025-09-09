package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import java.util.UUID

private val collaborationSessionStates: MutableList<CollaborationSessionState> = mutableListOf()

@Repository
class CollaborationSessionStateRepository {
    fun findByDocumentId(documentId: UUID): CollaborationSessionState =
        collaborationSessionStates.first { it.documentState.documentId == documentId }

    fun saveChanges(collaborationSessionState: CollaborationSessionState): CollaborationSessionState {
        if(collaborationSessionStates.none { it.id == collaborationSessionState.id }) throw IllegalArgumentException("CollaborationSessionState not found")

        collaborationSessionStates.removeIf { it.id == collaborationSessionState.id }
        if(!collaborationSessionStates.add(collaborationSessionState)) throw IllegalStateException("Failed to save changes")
        return collaborationSessionState
    }

    fun existsByDocumentId(documentId: UUID): Boolean {
        return collaborationSessionStates.any { it.documentState.documentId == documentId }
    }

    fun create(collaborationSessionState: CollaborationSessionState) {
        if(collaborationSessionStates.any { it.id == collaborationSessionState.id }) throw IllegalArgumentException("CollaborationSessionState already exists")
        if(!collaborationSessionStates.add(collaborationSessionState)) throw IllegalStateException("Failed to create CollaborationSessionState")
    }
}