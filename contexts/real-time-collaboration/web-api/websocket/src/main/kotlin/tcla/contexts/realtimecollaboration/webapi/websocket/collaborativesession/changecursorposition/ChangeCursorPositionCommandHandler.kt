package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.ChangeCursorPositionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.CollaborativeRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterIsCollaborator
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterOwnsCollaboratorState
import java.time.Instant

@Component
class ChangeCursorPositionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeRequestRepository: CollaborativeRequestRepository,
) {
    fun execute(command: ChangeCursorPositionCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. ChangeCursorPositionCommandHandler: $command")
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        ensureRequesterIsCollaborator(collaborativeSession = collaborativeSession, requesterId = command.requesterId)
        val collaboratorState =
            collaborativeSession.findCollaboratorStateByCollaboratorId(command.collaboratorId)
        ensureRequesterOwnsCollaboratorState(collaboratorState, command.requesterId)

        val collaborativeRequest = ChangeCursorPositionRequest(
            newPosition = command.newPosition,
            collaborativeSessionId = command.collaborativeSessionId,
            collaboratorId = command.collaboratorId,
            sequenceNumber = command.sequenceNumber,
            status = CollaborativeRequest.Status.PENDING
        )

        collaborativeRequestRepository.create(collaborativeRequest)
    }
}