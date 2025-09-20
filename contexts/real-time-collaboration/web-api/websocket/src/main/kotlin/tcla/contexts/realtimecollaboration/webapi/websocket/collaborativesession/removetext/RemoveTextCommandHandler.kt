package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.RemoveTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.CollaborativeRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterIsCollaborator
import java.time.Instant

@Component
class RemoveTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeRequestRepository: CollaborativeRequestRepository,
) {
    fun execute(command: RemoveTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. RemoveTextCommandHandler: $command")
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        ensureRequesterIsCollaborator(collaborativeSession = collaborativeSession, requesterId = command.requesterId)

        val collaborativeRequest = RemoveTextRequest(
            position = command.position,
            length = command.length,
            collaborativeSessionId = command.collaborativeSessionId,
            collaboratorId = command.collaboratorId,
            sequenceNumber = command.sequenceNumber,
            status = CollaborativeRequest.Status.PENDING
        )

        collaborativeRequestRepository.create(collaborativeRequest)
    }
}