package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.DocumentState
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaboratorJoined
import java.util.UUID

@Component
class AddCollaboratorToSessionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(command: AddCollaboratorToSessionCommand) {
        if(!collaborativeSessionRepository.existsByDocumentId(command.documentId)) {
            val collaborativeSession = CollaborativeSession(
                id = UUID.randomUUID(),
                documentState = DocumentState(documentId = command.documentId, content = ""),
                collaboratorStates = setOf(),
                lastCollaborativeEventSequenceNumber = 0L
            )
            collaborativeSessionRepository.create(collaborativeSession)
        }

        val collaborativeSession: CollaborativeSession = collaborativeSessionRepository.findByDocumentId(command.documentId)
        val nextSequenceNumber = collaborativeEventRepository.nextSequenceNumber()

        collaborativeSession.addCollaboratorState(
            CollaboratorState(
                userId = command.userId,
                collaboratorId = command.collaboratorId,
                cursorPosition = null,
                selectedText = null
            )
        )
        collaborativeSession.setLastCollaborativeEventSequenceNumber(nextSequenceNumber)

        collaborativeSessionRepository.saveChanges(collaborativeSession)

        collaborativeEventRepository.create(
            CollaboratorJoined(
                collaboratorId = command.collaboratorId,
                collaborativeSessionId = collaborativeSession.id,
                sequenceNumber = nextSequenceNumber,
                broadcasted = false,
            )
        )
    }

}