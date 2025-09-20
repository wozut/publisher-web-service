package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterIsCollaborator
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.AddTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.CollaborativeRequest
import java.time.Instant

@Component
class AddTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeRequestRepository: CollaborativeRequestRepository,
) {
    fun execute(command: AddTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. AddTextCommandHandler: $command")
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        ensureRequesterIsCollaborator(collaborativeSession = collaborativeSession, requesterId = command.requesterId)

        val collaborativeRequest = AddTextRequest(
            position = command.position,
            text = command.text,
            collaboratorId = command.collaboratorId,
            collaborativeSessionId = command.collaborativeSessionId,
            sequenceNumber = command.sequenceNumber,
            status = CollaborativeRequest.Status.PENDING
        )

        collaborativeRequestRepository.create(collaborativeRequest)
    }
}
