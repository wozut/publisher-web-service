package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaborativeEvent

@Component
class BroadcastPendingMessages(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val collaborativeEventRepository: CollaborativeEventRepository,
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
) {
    @Scheduled(fixedDelay = 100L, initialDelay = 100L, timeUnit = java.util.concurrent.TimeUnit.MILLISECONDS)
    fun execute() {
        val oldestPendingEvent: CollaborativeEvent? = collaborativeEventRepository.findOldestByBroadcasted(false)
        if (oldestPendingEvent == null) return

        oldestPendingEvent.markAsBroadcasted()
        collaborativeEventRepository.saveChanges(oldestPendingEvent)

        val collaborativeSession = collaborativeSessionRepository.findById(oldestPendingEvent.collaborativeSessionId)

        simpMessagingTemplate.convertAndSend(
            "/topic/updates/${collaborativeSession.documentState.documentId}",
            oldestPendingEvent
        )
    }
}