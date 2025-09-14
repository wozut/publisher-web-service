package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CreateCollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaboratorJoined

@Component
class AddCollaboratorToSessionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
    private val createCollaborativeSession: CreateCollaborativeSession
) {
    fun execute(command: AddCollaboratorToSessionCommand) {
        if(command.requesterId != command.collaboratorId) throw IllegalArgumentException()

        if (!collaborativeSessionRepository.existsByDocumentId(documentId = command.documentId)) {
            createCollaborativeSession.execute(documentId = command.documentId)
        }

        val collaborativeSession: CollaborativeSession =
            collaborativeSessionRepository.findByDocumentId(command.documentId)

        val collaboratorState = CollaboratorState(
            userId = command.requesterId,
            collaboratorId = command.collaboratorId,
            cursorPosition = null,
            selectedText = null
        )

        var updatedCollaborativeSession = collaborativeSession
            .addCollaboratorState(collaboratorState)
            .incrementLastCollaborativeEventSequenceNumber()

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)

        collaborativeEventRepository.create(
            CollaboratorJoined(
                collaboratorId = command.collaboratorId,
                collaborativeSessionId = updatedCollaborativeSession.id,
                sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
                broadcasted = false,
            )
        )
    }

}