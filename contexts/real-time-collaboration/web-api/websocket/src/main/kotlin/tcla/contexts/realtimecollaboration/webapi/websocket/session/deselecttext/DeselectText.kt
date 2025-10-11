package tcla.contexts.realtimecollaboration.webapi.websocket.session.deselecttext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.DeselectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextDeselected

@Service
class DeselectText(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(request: DeselectTextRequest) {
        val session = sessionRepository.findById(request.sessionId)
        var updatedSession = session.deselectText(
            writerId = request.writerId
        )

        updatedSession = sessionRepository.saveChanges(updatedSession)
        val textDeselected = TextDeselected(
            sessionId = updatedSession.id,
            writerId = request.writerId,
            sequenceNumber = updatedSession.lastSessionEventSequenceNumber,
            broadcasted = false
        )
        sessionEventRepository.create(textDeselected)
    }
}