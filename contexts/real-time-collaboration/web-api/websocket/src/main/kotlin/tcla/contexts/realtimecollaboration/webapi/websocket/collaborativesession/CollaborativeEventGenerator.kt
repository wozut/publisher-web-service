package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession

import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaborativeEvent

interface CollaborativeEventGenerator {
    fun popAllGeneratedCollaborativeEvents(): List<CollaborativeEvent>
}