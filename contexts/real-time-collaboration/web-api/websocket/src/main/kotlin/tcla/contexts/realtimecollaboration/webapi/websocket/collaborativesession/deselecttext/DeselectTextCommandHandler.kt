package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.DeselectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.CollaborativeRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterIsCollaborator
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterOwnsCollaboratorState
import java.time.Instant

@Component
class DeselectTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeRequestRepository: CollaborativeRequestRepository,
) {
    fun execute(command: DeselectTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. DeselectTextCommandHandler: $command")
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        ensureRequesterIsCollaborator(collaborativeSession = collaborativeSession, requesterId = command.requesterId)
        val collaboratorState =
            collaborativeSession.findCollaboratorStateByCollaboratorId(command.collaboratorId)
        ensureRequesterOwnsCollaboratorState(collaboratorState, command.requesterId)

        val collaborativeRequest = DeselectTextRequest(
            collaborativeSessionId = command.collaborativeSessionId,
            collaboratorId = command.collaboratorId,
            sequenceNumber = command.sequenceNumber,
            status = CollaborativeRequest.Status.PENDING
        )

        collaborativeRequestRepository.create(collaborativeRequest)
    }
}