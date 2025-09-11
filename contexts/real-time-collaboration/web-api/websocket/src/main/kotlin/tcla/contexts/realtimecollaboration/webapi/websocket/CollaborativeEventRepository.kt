package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Repository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaborativeEvent

private val collaborativeEvents: MutableList<CollaborativeEvent> = mutableListOf()

@Repository
class CollaborativeEventRepository {
    val lastSequenceNumber: Long = 0L

    fun nextSequenceNumber(): Long = lastSequenceNumber + 1L

    fun create(collaborativeEvent: CollaborativeEvent) {
        if (collaborativeEvents.any { it.sequenceNumber == collaborativeEvent.sequenceNumber }) throw IllegalArgumentException(
            "CollaborativeEvent already exists"
        )
        if(collaborativeEvent.sequenceNumber != lastSequenceNumber + 1L) throw IllegalArgumentException()

        if (!collaborativeEvents.add(collaborativeEvent)) throw IllegalStateException("Failed to create CollaborativeEvent")
    }

    fun findOldestByBroadcasted(broadcasted: Boolean): CollaborativeEvent? = collaborativeEvents
        .filter { it.broadcasted == broadcasted }
        .minByOrNull { it.sequenceNumber }

    fun saveChanges(collaborativeEvent: CollaborativeEvent): CollaborativeEvent {
        if (collaborativeEvents.none { it.sequenceNumber == collaborativeEvent.sequenceNumber }) throw IllegalArgumentException(
            "CollaborativeEvent not found"
        )

        collaborativeEvents.removeIf { it.sequenceNumber == collaborativeEvent.sequenceNumber }
        if (!collaborativeEvents.add(collaborativeEvent)) throw IllegalStateException("Failed to save changes")
        return collaborativeEvent
    }
}