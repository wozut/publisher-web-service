package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.RemoveTextRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextRemoved

@Service
class RemoveText(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(request: RemoveTextRequest) {
        val collaborativeSession = collaborativeSessionRepository.findById(request.collaborativeSessionId)
        var updatedCollaborativeSession = collaborativeSession.removeText(
            position = request.position,
            length = request.length
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textRemoved = TextRemoved(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = request.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            position = request.position,
            length = request.length
        )
        collaborativeEventRepository.create(textRemoved)
    }
}