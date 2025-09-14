package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CursorPositionChanged

@Component
class ChangeCursorPositionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: ChangeCursorPositionCommand) {
        if (command.requesterId != command.collaboratorId) throw IllegalArgumentException()
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)

        collaborativeSession.changeCursorPosition(
            collaboratorId = command.collaboratorId,
            newPosition = command.newPosition
        ).incrementLastCollaborativeEventSequenceNumber()

        val updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(collaborativeSession)
        val cursorPositionChanged = CursorPositionChanged(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = command.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            newPosition = command.newPosition
        )
        collaborativeEventRepository.create(cursorPositionChanged)
    }
}