package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.ChangeCursorPositionRequest
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CursorPositionChanged

@Service
class ChangeCursorPosition(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(request: ChangeCursorPositionRequest) {
        val collaborativeSession = collaborativeSessionRepository.findById(request.collaborativeSessionId)
        var updatedCollaborativeSession = collaborativeSession.changeCursorPosition(
            collaboratorId = request.collaboratorId,
            newPosition = request.newPosition
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val cursorPositionChanged = CursorPositionChanged(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = request.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            newPosition = request.newPosition
        )
        collaborativeEventRepository.create(cursorPositionChanged)
    }
}