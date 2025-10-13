package tcla.contexts.realtimecollaboration.webapi.websocket.session.addnewwriterstate

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.WriterState
import tcla.contexts.realtimecollaboration.webapi.websocket.events.WriterJoined
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import java.util.*

@Service
class AddNewWriterStateToSession(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(session: Session, requesterId: UUID): Session {
        val writerId = UUID.randomUUID()
        val writerState = WriterState(
            userId = requesterId,
            writerId = writerId,
            cursorPosition = null,
            selectedText = null
        )

        var updatedSession = session.addWriterState(writerState)

        updatedSession = sessionRepository.saveChanges(updatedSession)

        sessionEventRepository.createAll(updatedSession.popAllGeneratedSessionEvents())
        return updatedSession
    }
}