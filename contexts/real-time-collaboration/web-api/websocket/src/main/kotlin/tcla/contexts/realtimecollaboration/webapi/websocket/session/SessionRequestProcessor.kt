package tcla.contexts.realtimecollaboration.webapi.websocket.session

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.ChangeCursorPositionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SessionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.addtext.AddText
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SelectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.DeselectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.RemoveTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.session.changecursorposition.ChangeCursorPosition
import tcla.contexts.realtimecollaboration.webapi.websocket.session.selecttext.SelectText
import tcla.contexts.realtimecollaboration.webapi.websocket.session.deselecttext.DeselectText
import tcla.contexts.realtimecollaboration.webapi.websocket.session.removetext.RemoveText
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.AddTextRequest
import java.time.Instant
import java.util.concurrent.TimeUnit

@Component
class SessionRequestProcessor(
    private val sessionRequestRepository: SessionRequestRepository,
    private val changeCursorPosition: ChangeCursorPosition,
    private val selectText: SelectText,
    private val deselectText: DeselectText,
    private val addText: AddText,
    private val removeText: RemoveText,
) {
    @Scheduled(fixedDelay = 100L, initialDelay = 100L, timeUnit = TimeUnit.MILLISECONDS)
    @Synchronized
    fun execute() {

        var sessionRequest: SessionRequest =
            sessionRequestRepository.findOldestByStatus(SessionRequest.Status.PENDING) ?: return

        if(sessionRequest.sequenceNumber > 0L) {
            val exists: Boolean =
                sessionRequestRepository.existsBySessionAndWriterAndSequenceNumberAndStatus(
                    sessionRequest.sessionId,
                    sessionRequest.writerId,
                    sessionRequest.sequenceNumber - 1,
                    SessionRequest.Status.PROCESSED,
                )
            if(!exists) return
        }

        sessionRequest.markAsProcessing()
        sessionRequest = sessionRequestRepository.saveChanges(sessionRequest)

        when(sessionRequest) {
            is ChangeCursorPositionRequest -> changeCursorPosition.execute(sessionRequest)
            is SelectTextRequest -> selectText.execute(sessionRequest)
            is DeselectTextRequest -> deselectText.execute(sessionRequest)
            is AddTextRequest -> addText.execute(sessionRequest)
            is RemoveTextRequest -> removeText.execute(sessionRequest)
        }

        sessionRequest.markAsProcessed()
        sessionRequest = sessionRequestRepository.saveChanges(sessionRequest)
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. SessionRequest: $sessionRequest")
    }
}