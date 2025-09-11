package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import java.util.UUID

private val collaborativeSessions: MutableList<CollaborativeSession> = mutableListOf()

@Repository
class CollaborativeSessionRepository {
    fun findByDocumentId(documentId: UUID): CollaborativeSession =
        collaborativeSessions.first { it.documentState.documentId == documentId }

    fun findById(id: UUID): CollaborativeSession = collaborativeSessions.first { it.id == id }

    fun saveChanges(collaborativeSession: CollaborativeSession): CollaborativeSession {
        if(collaborativeSessions.none { it.id == collaborativeSession.id }) throw IllegalArgumentException("CollaborativeSession not found")

        collaborativeSessions.removeIf { it.id == collaborativeSession.id }
        if(!collaborativeSessions.add(collaborativeSession)) throw IllegalStateException("Failed to save changes")
        return collaborativeSession
    }

    fun existsByDocumentId(documentId: UUID): Boolean {
        return collaborativeSessions.any { it.documentState.documentId == documentId }
    }

    fun create(collaborativeSession: CollaborativeSession) {
        if(collaborativeSessions.any { it.id == collaborativeSession.id }) throw IllegalArgumentException("CollaborativeSession already exists")
        if(!collaborativeSessions.add(collaborativeSession)) throw IllegalStateException("Failed to create CollaborativeSession")
    }
}