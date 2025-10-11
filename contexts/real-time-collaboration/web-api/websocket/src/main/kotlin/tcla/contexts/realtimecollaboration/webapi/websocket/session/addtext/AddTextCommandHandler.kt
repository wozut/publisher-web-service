package tcla.contexts.realtimecollaboration.webapi.websocket.session.addtext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterIsWriter
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.AddTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SessionRequest
import java.time.Instant

@Component
class AddTextCommandHandler(
    private val sessionRepository: SessionRepository,
    private val sessionRequestRepository: SessionRequestRepository,
) {
    fun execute(command: AddTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. AddTextCommandHandler: $command")
        val session = sessionRepository.findById(command.sessionId)
        ensureRequesterIsWriter(session = session, requesterId = command.requesterId)

        val sessionRequest = AddTextRequest(
            position = command.position,
            text = command.text,
            writerId = command.writerId,
            sessionId = command.sessionId,
            sequenceNumber = command.sequenceNumber,
            status = SessionRequest.Status.PENDING
        )

        sessionRequestRepository.create(sessionRequest)
    }
}
