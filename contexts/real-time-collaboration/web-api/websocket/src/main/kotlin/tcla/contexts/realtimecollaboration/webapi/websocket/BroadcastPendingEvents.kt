package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.SessionEvent

@Component
class BroadcastPendingEvents(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val sessionEventRepository: SessionEventRepository,
    private val sessionRepository: SessionRepository,
) {
    @Scheduled(fixedDelay = 1L, initialDelay = 1L, timeUnit = java.util.concurrent.TimeUnit.MILLISECONDS)
    @Synchronized
    fun execute() {
        val oldestPendingEvent: SessionEvent =
            sessionEventRepository.findOldestByBroadcasted(false) ?: return

        var updatedEvent = oldestPendingEvent.markAsBroadcasted()
        updatedEvent = sessionEventRepository.saveChanges(updatedEvent)

        val session = sessionRepository.findById(updatedEvent.sessionId)

        simpMessagingTemplate.convertAndSend(
            "/topic/updates/${session.documentState.documentId}",
            updatedEvent
        )
        println("Broadcasting event $updatedEvent")
    }
}