package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.SelectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextSelected

@Service
class SelectText(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(request: SelectTextRequest) {
        val collaborativeSession = collaborativeSessionRepository.findById(request.collaborativeSessionId)
        var updatedCollaborativeSession = collaborativeSession.selectText(
            collaboratorId = request.collaboratorId,
            position = request.position,
            length = request.length
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textSelected = TextSelected(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = request.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            position = request.position,
            length = request.length
        )
        collaborativeEventRepository.create(textSelected)
    }
}