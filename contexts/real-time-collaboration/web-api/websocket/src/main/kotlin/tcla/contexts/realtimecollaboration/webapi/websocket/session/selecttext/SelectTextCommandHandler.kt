package tcla.contexts.realtimecollaboration.webapi.websocket.session.selecttext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SelectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SessionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterIsWriterInSession
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterOwnsWriterState
import java.time.Instant

@Component
class SelectTextCommandHandler(
    private val sessionRepository: SessionRepository,
    private val sessionRequestRepository: SessionRequestRepository,
) {
    fun execute(command: SelectTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. SelectTextCommandHandler: $command")
        val session = sessionRepository.findById(command.sessionId)
        ensureRequesterIsWriterInSession(session = session, requesterId = command.requesterId)
        val writerState =
            session.findWriterStateByWriterId(command.writerId)
        ensureRequesterOwnsWriterState(writerState, command.requesterId)

        val sessionRequest = SelectTextRequest(
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