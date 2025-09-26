package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaborativeEvent

private val collaborativeEventsCollection: MutableList<CollaborativeEvent> = mutableListOf()

@Repository
class CollaborativeEventRepository {


    @Synchronized
    fun create(collaborativeEvent: CollaborativeEvent) {
        if (collaborativeEventsCollection.any { it.sequenceNumber == collaborativeEvent.sequenceNumber }) throw IllegalArgumentException(
            "CollaborativeEvent already exists"
        )

        if (!collaborativeEventsCollection.add(collaborativeEvent)) throw IllegalStateException("Failed to create CollaborativeEvent")
    }

    @Synchronized
    fun createAll(collaborativeEvents: List<CollaborativeEvent>) {
        collaborativeEvents.forEach { create(it) }
    }

    fun findOldestByBroadcasted(broadcasted: Boolean): CollaborativeEvent? = collaborativeEventsCollection
        .filter { it.broadcasted == broadcasted }
        .minByOrNull { it.sequenceNumber }

    @Synchronized
    fun saveChanges(collaborativeEvent: CollaborativeEvent): CollaborativeEvent {
        if (collaborativeEventsCollection.none { it.sequenceNumber == collaborativeEvent.sequenceNumber }) throw IllegalArgumentException(
            "CollaborativeEvent not found"
        )

        collaborativeEventsCollection.removeIf { it.collaborativeSessionId == collaborativeEvent.collaborativeSessionId && it.sequenceNumber == collaborativeEvent.sequenceNumber }
        if (!collaborativeEventsCollection.add(collaborativeEvent)) throw IllegalStateException("Failed to save changes")
        return collaborativeEvent
    }
}