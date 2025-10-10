package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaborativeEvent

@Component
class BroadcastPendingEvents(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val collaborativeEventRepository: CollaborativeEventRepository,
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
) {
    @Scheduled(fixedDelay = 1L, initialDelay = 1L, timeUnit = java.util.concurrent.TimeUnit.MILLISECONDS)
    @Synchronized
    fun execute() {
        val oldestPendingEvent: CollaborativeEvent =
            collaborativeEventRepository.findOldestByBroadcasted(false) ?: return

        var updatedEvent = oldestPendingEvent.markAsBroadcasted()
        updatedEvent = collaborativeEventRepository.saveChanges(updatedEvent)

        val collaborativeSession = collaborativeSessionRepository.findById(updatedEvent.collaborativeSessionId)

        simpMessagingTemplate.convertAndSend(
            "/topic/updates/${collaborativeSession.documentState.documentId}",
            updatedEvent
        )
        println("Broadcasting event $updatedEvent")
    }
}