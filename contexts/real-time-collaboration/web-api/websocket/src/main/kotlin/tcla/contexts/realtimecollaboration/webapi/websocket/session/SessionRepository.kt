package tcla.contexts.realtimecollaboration.webapi.websocket.session

import org.springframework.stereotype.Repository
import java.util.UUID

private val sessions: MutableList<Session> = mutableListOf()

@Repository
class SessionRepository {
    fun findByDocumentId(documentId: UUID): Session =
        sessions.first { it.documentState.documentId == documentId }

    fun findByDocumentIdAndStatus(documentId: UUID, status: Session.Status): Session =
        sessions.first { it.documentState.documentId == documentId && it.status == status }

    fun findById(id: UUID): Session = sessions.first { it.id == id }

    @Synchronized
    fun saveChanges(session: Session): Session {
        if (sessions.none { it.id == session.id }) throw IllegalArgumentException("CollaborativeSession not found")

        sessions.removeIf { it.id == session.id }
        if (!sessions.add(session)) throw IllegalStateException("Failed to save changes")
        return session
    }

    fun existsByDocumentIdAndStatus(documentId: UUID, status: Session.Status): Boolean {
        return sessions.any { it.documentState.documentId == documentId && it.status == status}
    }

    fun existsByDocumentId(documentId: UUID): Boolean {
        return sessions.any { it.documentState.documentId == documentId }
    }

    @Synchronized
    fun create(session: Session): Session {
        if (sessions.any { it.id == session.id }) throw IllegalArgumentException("CollaborativeSession already exists")

        if (!sessions.add(session)) throw IllegalStateException("Failed to create CollaborativeSession")
        return session
    }
}