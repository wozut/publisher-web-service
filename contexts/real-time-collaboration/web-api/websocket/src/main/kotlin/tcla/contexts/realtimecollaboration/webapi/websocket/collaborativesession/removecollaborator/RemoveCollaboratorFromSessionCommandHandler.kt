package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removecollaborator

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaboratorLeft

@Component
class RemoveCollaboratorFromSessionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: RemoveCollaboratorFromSessionCommand) {
        if(command.requesterId != command.collaboratorId) throw IllegalArgumentException()

        if (!collaborativeSessionRepository.existsByDocumentId(documentId = command.documentId)) {
            throw IllegalArgumentException("Collaborative session not found for document: ${command.documentId}")
        }

        val collaborativeSession: CollaborativeSession =
            collaborativeSessionRepository.findByDocumentId(command.documentId)

        var updatedCollaborativeSession = collaborativeSession
            .removeCollaboratorState(command.collaboratorId)

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)

        collaborativeEventRepository.create(
            CollaboratorLeft(
                collaboratorId = command.collaboratorId,
                collaborativeSessionId = updatedCollaborativeSession.id,
                sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
                broadcasted = false,
            )
        )
    }

}