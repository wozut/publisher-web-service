package tcla.contexts.realtimecollaboration.webapi.websocket.messages

abstract class SessionMessage {
    abstract val writerId: String
    abstract val sessionId: String
    abstract val sequenceNumber: Long
}