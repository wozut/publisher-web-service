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
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        val collaboratorState =
            collaborativeSession.findCollaboratorStateByCollaboratorId(command.collaboratorId)
        if (collaboratorState.userId != command.requesterId) throw IllegalArgumentException()

        var updatedCollaborativeSession = collaborativeSession.changeCursorPosition(
            collaboratorId = command.collaboratorId,
            newPosition = command.newPosition
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
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