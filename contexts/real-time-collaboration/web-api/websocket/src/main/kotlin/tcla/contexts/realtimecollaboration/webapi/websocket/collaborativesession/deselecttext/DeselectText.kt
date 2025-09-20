package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.DeselectTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextDeselected

@Service
class DeselectText(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(request: DeselectTextRequest) {
        val collaborativeSession = collaborativeSessionRepository.findById(request.collaborativeSessionId)
        var updatedCollaborativeSession = collaborativeSession.deselectText(
            collaboratorId = request.collaboratorId
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textDeselected = TextDeselected(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = request.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false
        )
        collaborativeEventRepository.create(textDeselected)
    }
}