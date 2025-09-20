package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.ChangeCursorPositionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.CollaborativeRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext.AddText
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SelectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.DeselectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPosition
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext.SelectText
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext.DeselectText
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.AddTextRequest
import java.util.concurrent.TimeUnit

@Component
class CollaborativeRequestProcessor(
    private val collaborativeRequestRepository: CollaborativeRequestRepository,
    private val changeCursorPosition: ChangeCursorPosition,
    private val selectText: SelectText,
    private val deselectText: DeselectText,
    private val addText: AddText,
) {
    @Scheduled(fixedDelay = 100L, initialDelay = 100L, timeUnit = TimeUnit.MILLISECONDS)
    @Synchronized
    fun execute() {

        var collaborativeRequest: CollaborativeRequest =
            collaborativeRequestRepository.findOldestByStatus(CollaborativeRequest.Status.PENDING) ?: return

        if(collaborativeRequest.sequenceNumber > 0L) {
            val exists: Boolean =
                collaborativeRequestRepository.existsByCollaborativeSessionAndCollaboratorAndSequenceNumberAndStatus(
                    collaborativeRequest.collaborativeSessionId,
                    collaborativeRequest.collaboratorId,
                    collaborativeRequest.sequenceNumber - 1,
                    CollaborativeRequest.Status.PROCESSED,
                )
            if(!exists) return
        }

        collaborativeRequest.markAsProcessing()
        collaborativeRequest = collaborativeRequestRepository.saveChanges(collaborativeRequest)

        when(collaborativeRequest) {
            is ChangeCursorPositionRequest -> changeCursorPosition.execute(collaborativeRequest)
            is SelectTextRequest -> selectText.execute(collaborativeRequest)
            is DeselectTextRequest -> deselectText.execute(collaborativeRequest)
            is AddTextRequest -> addText.execute(collaborativeRequest)
        }

        collaborativeRequest.markAsProcessed()
        collaborativeRequestRepository.saveChanges(collaborativeRequest)
    }
}