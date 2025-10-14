package tcla.contexts.realtimecollaboration.webapi.websocket.session.removewriterstate

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import java.util.*

@Service
class RemoveWriterStateFromSession(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(session: Session, requesterId: UUID): Session {
        var updatedSession = session.removeWriterState(requesterId)

        updatedSession = sessionRepository.saveChanges(updatedSession)

        sessionEventRepository.createAll(updatedSession.popAllGeneratedSessionEvents())
        return updatedSession
    }
}