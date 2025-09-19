package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.ChangeCursorPositionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeRequestRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPosition
import java.util.concurrent.TimeUnit

@Component
class CollaborativeRequestProcessor(
    private val collaborativeRequestRepository: CollaborativeRequestRepository,
    private val changeCursorPosition: ChangeCursorPosition,
) {
    @Scheduled(fixedDelay = 100L, initialDelay = 100L, timeUnit = TimeUnit.MILLISECONDS)
    @Synchronized
    fun execute() {

        var collaborativeRequest: CollaborativeRequest? = collaborativeRequestRepository.findOldestByStatus(CollaborativeRequest.Status.PENDING)

        if(collaborativeRequest == null) return

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
        }

        collaborativeRequest.markAsProcessed()
        collaborativeRequestRepository.saveChanges(collaborativeRequest)
    }
}