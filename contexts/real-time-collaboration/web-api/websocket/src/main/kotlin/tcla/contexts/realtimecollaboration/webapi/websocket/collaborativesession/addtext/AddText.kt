package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextAdded
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.AddTextRequest

@Service
class AddText(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(request: AddTextRequest) {
        val collaborativeSession = collaborativeSessionRepository.findById(request.collaborativeSessionId)
        var updatedCollaborativeSession = collaborativeSession.addText(
            text = request.text,
            position = request.position
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textAdded = TextAdded(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = request.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            position = request.position,
            text = request.text
        )
        collaborativeEventRepository.create(textAdded)
    }
}