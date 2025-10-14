package tcla.contexts.realtimecollaboration.webapi.websocket.session.selecttext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SelectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextSelected

@Service
class SelectText(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
) {
    fun execute(request: SelectTextRequest) {
        val session = sessionRepository.findById(request.sessionId)
        var updatedSession = session.selectText(
            writerId = request.writerId,
            position = request.position,
            length = request.length
        )

        updatedSession = sessionRepository.saveChanges(updatedSession)
        val textSelected = TextSelected(
            sessionId = updatedSession.id,
            writerId = request.writerId,
            sequenceNumber = updatedSession.lastSessionEventSequenceNumber,
            broadcasted = false,
            position = request.position,
            length = request.length
        )
        sessionEventRepository.create(textSelected)
    }
}