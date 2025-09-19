package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterIsCollaborator
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterOwnsCollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextSelected

@Component
class SelectTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: SelectTextCommand) {
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        ensureRequesterIsCollaborator(collaborativeSession = collaborativeSession, requesterId = command.requesterId)
        val collaboratorState =
            collaborativeSession.findCollaboratorStateByCollaboratorId(command.collaboratorId)
        ensureRequesterOwnsCollaboratorState(collaboratorState, command.requesterId)

        var updatedCollaborativeSession = collaborativeSession.selectText(
            collaboratorId = command.collaboratorId,
            position = command.position,
            length = command.length
        )

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