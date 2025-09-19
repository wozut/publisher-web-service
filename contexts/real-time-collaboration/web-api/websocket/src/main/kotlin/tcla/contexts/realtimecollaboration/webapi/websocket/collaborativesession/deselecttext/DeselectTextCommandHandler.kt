package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterIsCollaborator
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterOwnsCollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextDeselected

@Component
class DeselectTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: DeselectTextCommand) {
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        ensureRequesterIsCollaborator(collaborativeSession = collaborativeSession, requesterId = command.requesterId)
        val collaboratorState =
            collaborativeSession.findCollaboratorStateByCollaboratorId(command.collaboratorId)
        ensureRequesterOwnsCollaboratorState(collaboratorState, command.requesterId)

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