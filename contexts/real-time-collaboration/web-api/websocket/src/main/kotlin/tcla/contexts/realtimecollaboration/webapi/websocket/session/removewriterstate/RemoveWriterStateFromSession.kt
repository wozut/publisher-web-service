package tcla.contexts.realtimecollaboration.webapi.websocket.session.removewriterstate

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.WriterLeft
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import java.util.UUID

@Service
class RemoveWriterStateFromSession(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(session: Session, requesterId: UUID): Session {
        var updatedSession = session
            .removeWriterState(requesterId)

        updatedSession = sessionRepository.saveChanges(updatedSession)

        val writerState = session.findWriterStateByUserId(requesterId)

        val writerLeft = WriterLeft(
            writerId = writerState.writerId,
            sessionId = updatedSession.id,
            sequenceNumber = updatedSession.lastSessionEventSequenceNumber,
            broadcasted = false,
        )
        sessionEventRepository.create(writerLeft)
        return updatedSession
    }
}