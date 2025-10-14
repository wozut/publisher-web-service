package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.SessionEvent

private val sessionEventsCollection: MutableList<SessionEvent> = mutableListOf()

@Repository
class SessionEventRepository {


    @Synchronized
    fun create(sessionEvent: SessionEvent) {
        if (sessionEventsCollection.any { it.sequenceNumber == sessionEvent.sequenceNumber }) throw IllegalArgumentException(
            "SessionEvent already exists"
        )

        if (!sessionEventsCollection.add(sessionEvent)) throw IllegalStateException("Failed to create CollaborativeEvent")
    }

    @Synchronized
    fun createAll(sessionEvents: List<SessionEvent>) {
        sessionEvents.forEach { create(it) }
    }

    fun findOldestByBroadcasted(broadcasted: Boolean): SessionEvent? = sessionEventsCollection
        .filter { it.broadcasted == broadcasted }
        .minByOrNull { it.sequenceNumber }

    @Synchronized
    fun saveChanges(sessionEvent: SessionEvent): SessionEvent {
        if (sessionEventsCollection.none { it.sequenceNumber == sessionEvent.sequenceNumber }) throw IllegalArgumentException(
            "CollaborativeEvent not found"
        )

        sessionEventsCollection.removeIf { it.sessionId == sessionEvent.sessionId && it.sequenceNumber == sessionEvent.sequenceNumber }
        if (!sessionEventsCollection.add(sessionEvent)) throw IllegalStateException("Failed to save changes")
        return sessionEvent
    }
}