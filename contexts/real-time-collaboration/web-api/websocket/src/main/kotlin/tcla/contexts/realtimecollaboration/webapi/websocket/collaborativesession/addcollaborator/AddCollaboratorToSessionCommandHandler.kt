package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
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
        if (!collaborativeSessionRepository.existsByDocumentId(command.documentId)) {
            val collaborativeSession = CollaborativeSession(
                id = UUID.randomUUID(),
                documentState = DocumentState(documentId = command.documentId, content = ""),
                collaboratorStates = setOf(),
                lastCollaborativeEventSequenceNumber = 0L
            )
            collaborativeSessionRepository.create(collaborativeSession)
        }

        val collaborativeSession: CollaborativeSession =
            collaborativeSessionRepository.findByDocumentId(command.documentId)
        val nextSequenceNumber = collaborativeEventRepository.nextSequenceNumber()

        val collaboratorState = CollaboratorState(
            userId = command.requesterId,
            collaboratorId = command.collaboratorId,
            cursorPosition = null,
            selectedText = null
        )

        var updatedCollaborativeSession = collaborativeSession
            .addCollaboratorState(collaboratorState)
            .setLastCollaborativeEventSequenceNumber(nextSequenceNumber)

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)

        collaborativeEventRepository.create(
            CollaboratorJoined(
                collaboratorId = command.collaboratorId,
                collaborativeSessionId = updatedCollaborativeSession.id,
                sequenceNumber = nextSequenceNumber,
                broadcasted = false,
            )
        )
    }

}