package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.join

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CreateCollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaboratorJoined
import java.util.UUID

@Component
class JoinToSessionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
    private val createCollaborativeSession: CreateCollaborativeSession
) {
    fun execute(command: JoinToSessionCommand) {
        if (!collaborativeSessionRepository.existsByDocumentId(documentId = command.documentId)) {
            createCollaborativeSession.execute(documentId = command.documentId)
        }

        val collaborativeSession: CollaborativeSession =
            collaborativeSessionRepository.findByDocumentId(command.documentId)

        val collaboratorId = UUID.randomUUID()
        val collaboratorState = CollaboratorState(
            userId = command.requesterId,
            collaboratorId = collaboratorId,
            cursorPosition = null,
            selectedText = null
        )

        var updatedCollaborativeSession = collaborativeSession
            .addCollaboratorState(collaboratorState)

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)

        collaborativeEventRepository.create(
            CollaboratorJoined(
                collaboratorId = collaboratorId,
                collaborativeSessionId = updatedCollaborativeSession.id,
                sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
                broadcasted = false,
            )
        )
    }

}