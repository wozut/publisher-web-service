package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextDeselected

@Component
class DeselectTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: DeselectTextCommand) {
        if (command.requesterId != command.collaboratorId) throw IllegalArgumentException()
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)

        var updatedCollaborativeSession = collaborativeSession.deselectText(
            collaboratorId = command.collaboratorId
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textDeselected = TextDeselected(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = command.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false
        )
        collaborativeEventRepository.create(textDeselected)
    }
}