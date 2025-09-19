package tcla.contexts.realtimecollaboration.webapi.websocket

abstract class CollaborativeMessage {
    abstract val collaboratorId: String
    abstract val collaborativeSessionId: String
    abstract val sequenceNumber: Long
}
