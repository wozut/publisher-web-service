package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaboratorJoined
import java.util.*

@Component
class AddCollaboratorToSessionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(collaboratorId: UUID, documentId: UUID) {
        if(!collaborativeSessionRepository.existsByDocumentId(documentId)) {
            val collaborativeSession = CollaborativeSession(
                id = UUID.randomUUID(),
                documentState = DocumentState(documentId = documentId, content = ""),
                collaboratorStates = setOf(),
                lastCollaborativeEventSequenceNumber = 0L
            )
            collaborativeSessionRepository.create(collaborativeSession)
        }

        val collaborativeSession: CollaborativeSession = collaborativeSessionRepository.findByDocumentId(documentId)
        val nextSequenceNumber = collaborativeEventRepository.nextSequenceNumber()

        collaborativeSession.addCollaboratorState(CollaboratorState(
            collaboratorId = collaboratorId,
            cursorPosition = null,
            selectedText = null
        ))
        collaborativeSession.setLastCollaborativeEventSequenceNumber(nextSequenceNumber)

        collaborativeSessionRepository.saveChanges(collaborativeSession)

        collaborativeEventRepository.create(CollaboratorJoined(
            collaboratorId = collaboratorId,
            collaborativeSessionId = collaborativeSession.id,
            sequenceNumber = nextSequenceNumber,
            broadcasted = false,
        ))
    }

}