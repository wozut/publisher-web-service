package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextSelected

@Component
class SelectTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: SelectTextCommand) {
        if (command.requesterId != command.collaboratorId) throw IllegalArgumentException()
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)

        var updatedCollaborativeSession = collaborativeSession.selectText(
            collaboratorId = command.collaboratorId,
            position = command.position,
            length = command.length
        ).incrementLastCollaborativeEventSequenceNumber()

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)
        val textSelected = TextSelected(
            collaborativeSessionId = updatedCollaborativeSession.id,
            collaboratorId = command.collaboratorId,
            sequenceNumber = updatedCollaborativeSession.lastCollaborativeEventSequenceNumber,
            broadcasted = false,
            position = command.position,
            length = command.length
        )
        collaborativeEventRepository.create(textSelected)
    }
}