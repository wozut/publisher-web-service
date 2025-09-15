package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextRemoved

@Component
class RemoveTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: RemoveTextCommand) {
        if (command.requesterId != command.collaboratorId) throw IllegalArgumentException()
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)

        var updatedCollaborativeSession = collaborativeSession.removeText(
            position = command.position,
            length = command.length
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textRemoved = TextRemoved(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = command.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            position = command.position,
            length = command.length
        )
        collaborativeEventRepository.create(textRemoved)
    }
}