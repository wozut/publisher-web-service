package tcla.contexts.realtimecollaboration.webapi.websocket.session.changecursorposition

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.ChangeCursorPositionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SessionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterIsWriter
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterOwnsWriterState
import java.time.Instant

@Component
class ChangeCursorPositionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val sessionRequestRepository: SessionRequestRepository,
) {
    fun execute(command: ChangeCursorPositionCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. ChangeCursorPositionCommandHandler: $command")
        val session = sessionRepository.findById(command.sessionId)
        ensureRequesterIsWriter(session = session, requesterId = command.requesterId)
        val writerState =
            session.findWriterStateByWriterId(command.writerId)
        ensureRequesterOwnsWriterState(writerState, command.requesterId)

        val sessionRequest = ChangeCursorPositionRequest(
            newPosition = command.newPosition,
            sessionId = command.sessionId,
            writerId = command.writerId,
            sequenceNumber = command.sequenceNumber,
            status = SessionRequest.Status.PENDING
        )

        sessionRequestRepository.create(sessionRequest)
    }
}