package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.leave

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaboratorLeft

@Component
class LeaveSessionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: LeaveSessionCommand) {
        if (!collaborativeSessionRepository.existsByDocumentId(documentId = command.documentId)) {
            throw IllegalArgumentException("Collaborative session not found for document: ${command.documentId}")
        }

        val collaborativeSession: CollaborativeSession =
            collaborativeSessionRepository.findByDocumentId(command.documentId)

        val collaboratorState = collaborativeSession.findCollaboratorState(command.requesterId)

        var updatedCollaborativeSession = collaborativeSession
            .removeCollaboratorState(command.requesterId)

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)

        collaborativeEventRepository.create(
            CollaboratorLeft(
                collaboratorId = collaboratorState.collaboratorId,
                collaborativeSessionId = updatedCollaborativeSession.id,
                sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
                broadcasted = false,
            )
        )
    }

}