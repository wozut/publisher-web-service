package tcla.contexts.realtimecollaboration.webapi.websocket.session.changecursorposition

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.ChangeCursorPositionRequest

@Service
class ChangeCursorPosition(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(request: ChangeCursorPositionRequest) {
        val session = sessionRepository.findById(request.sessionId)

        var updatedSession = session.changeCursorPosition(
            writerId = request.writerId,
            newPosition = request.newPosition
        )

        updatedSession = sessionRepository.saveChanges(updatedSession)

        sessionEventRepository.createAll(updatedSession.popAllGeneratedSessionEvents())
    }
}