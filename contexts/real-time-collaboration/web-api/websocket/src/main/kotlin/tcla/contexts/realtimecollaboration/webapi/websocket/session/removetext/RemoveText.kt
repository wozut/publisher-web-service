package tcla.contexts.realtimecollaboration.webapi.websocket.session.removetext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.RemoveTextRequest

@Service
class RemoveText(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(request: RemoveTextRequest) {
        val session = sessionRepository.findById(request.sessionId)

        var updatedSession = session.removeText(
            writerId = request.writerId,
            position = request.position,
            length = request.length
        )

        updatedSession = sessionRepository.saveChanges(updatedSession)

        sessionEventRepository.createAll(
            sessionEvents = updatedSession.popAllGeneratedSessionEvents()
        )
    }
}