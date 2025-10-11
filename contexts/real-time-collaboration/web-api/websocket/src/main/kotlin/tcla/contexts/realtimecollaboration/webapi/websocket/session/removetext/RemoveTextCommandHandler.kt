package tcla.contexts.realtimecollaboration.webapi.websocket.session.removetext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.RemoveTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SessionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterIsWriter
import java.time.Instant

@Component
class RemoveTextCommandHandler(
    private val sessionRepository: SessionRepository,
    private val sessionRequestRepository: SessionRequestRepository,
) {
    fun execute(command: RemoveTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. RemoveTextCommandHandler: $command")
        val session = sessionRepository.findById(command.sessionId)
        ensureRequesterIsWriter(session = session, requesterId = command.requesterId)

        val sessionRequest = RemoveTextRequest(
            position = command.position,
            length = command.length,
            sessionId = command.sessionId,
            writerId = command.writerId,
            sequenceNumber = command.sequenceNumber,
            status = SessionRequest.Status.PENDING
        )

        sessionRequestRepository.create(sessionRequest)
    }
}