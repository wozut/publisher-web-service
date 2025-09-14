package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository

@Component
class ChangeCursorPositionCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository
) {
    fun execute(command: ChangeCursorPositionCommand) {
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)

        collaborativeSession.changeCursorPosition(command.newPosition)
    }
}