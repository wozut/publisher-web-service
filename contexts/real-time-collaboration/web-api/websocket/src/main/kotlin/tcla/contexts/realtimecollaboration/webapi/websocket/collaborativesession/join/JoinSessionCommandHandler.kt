package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.join

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CreateCollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaboratorJoined
import java.util.UUID

@Component
class JoinSessionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
    private val createCollaborativeSession: CreateCollaborativeSession,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    fun execute(command: JoinSessionCommand) {
        if (!collaborativeSessionRepository.existsByDocumentId(documentId = command.documentId)) {
            createCollaborativeSession.execute(documentId = command.documentId)
        }

        //TODO: aplicar mismo patrón que en ChangeCursorPositionCommandHandler
        val collaborativeSession: CollaborativeSession =
            collaborativeSessionRepository.findByDocumentId(command.documentId)

        //TODO: fallar si ya existe?
        if(collaborativeSession.collaboratorExistsByUserId(userId = command.requesterId)) return

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

        val collaboratorJoined = CollaboratorJoined(
            collaboratorId = collaboratorId,
            collaborativeSessionId = updatedCollaborativeSession.id,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
        )
        collaborativeEventRepository.create(collaboratorJoined)

        simpMessagingTemplate.convertAndSendToUser(
            collaboratorState.userId.toString(),
            "/queue/collaborative-session-state/${updatedCollaborativeSession.documentState.documentId}",
            updatedCollaborativeSession
        )
    }

}