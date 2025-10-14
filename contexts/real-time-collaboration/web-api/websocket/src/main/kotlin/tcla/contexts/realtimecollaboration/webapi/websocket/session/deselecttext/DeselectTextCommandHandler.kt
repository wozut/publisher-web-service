package tcla.contexts.realtimecollaboration.webapi.websocket.session.deselecttext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.DeselectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SessionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterIsWriterInSession
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterOwnsWriterState
import java.time.Instant

@Component
class DeselectTextCommandHandler(
    private val sessionRepository: SessionRepository,
    private val sessionRequestRepository: SessionRequestRepository,
) {
    fun execute(command: DeselectTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. DeselectTextCommandHandler: $command")
        val session = sessionRepository.findById(command.sessionId)
        ensureRequesterIsWriterInSession(session = session, requesterId = command.requesterId)
        val writerState =
            session.findWriterStateByWriterId(command.writerId)
        ensureRequesterOwnsWriterState(writerState, command.requesterId)

        val sessionRequest = DeselectTextRequest(
            sessionId = command.sessionId,
            writerId = command.writerId,
            sequenceNumber = command.sequenceNumber,
            status = SessionRequest.Status.PENDING
        )

        sessionRequestRepository.create(sessionRequest)
    }
}