package tcla.contexts.realtimecollaboration.webapi.websocket.session.addtext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.AddTextRequest

@Service
class AddText(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(request: AddTextRequest) {
        val session = sessionRepository.findById(request.sessionId)

        var updatedSession = session.addText(
            writerId = request.writerId,
            text = request.text,
            position = request.position
        )

        updatedSession = sessionRepository.saveChanges(updatedSession)

        sessionEventRepository.createAll(
            sessionEvents = updatedSession.popAllGeneratedSessionEvents()
        )
    }
}