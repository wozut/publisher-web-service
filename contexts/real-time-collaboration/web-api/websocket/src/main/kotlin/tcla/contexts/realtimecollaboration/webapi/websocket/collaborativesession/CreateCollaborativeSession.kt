package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.DocumentState
import java.util.UUID

@Service
class CreateCollaborativeSession(
    private val collaborativeSessionRepository: CollaborativeSessionRepository
) {
    fun execute(documentId: UUID) {
        val collaborativeSession = CollaborativeSession(
            id = UUID.randomUUID(),
            documentState = DocumentState(documentId = documentId, content = ""),
            collaboratorStates = mutableSetOf(),
            lastCollaborativeEventSequenceNumber = 0L
        )
        collaborativeSessionRepository.create(collaborativeSession)
    }
}