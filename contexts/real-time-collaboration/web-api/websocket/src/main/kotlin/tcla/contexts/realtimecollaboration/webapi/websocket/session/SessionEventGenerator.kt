package tcla.contexts.realtimecollaboration.webapi.websocket.session

import tcla.contexts.realtimecollaboration.webapi.websocket.events.SessionEvent

interface SessionEventGenerator {
    fun popAllGeneratedSessionEvents(): List<SessionEvent>
}