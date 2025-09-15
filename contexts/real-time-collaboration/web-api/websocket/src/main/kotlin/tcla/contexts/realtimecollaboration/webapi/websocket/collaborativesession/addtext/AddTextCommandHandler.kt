package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextAdded

@Component
class AddTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: AddTextCommand) {
        if (command.requesterId != command.collaboratorId) throw IllegalArgumentException()
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)

        var updatedCollaborativeSession = collaborativeSession.addText(
            position = command.position,
            text = command.text
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textAdded = TextAdded(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = command.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            position = command.position,
            text = command.text
        )
        collaborativeEventRepository.create(textAdded)
    }
}