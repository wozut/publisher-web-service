package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules.ensureRequesterIsCollaborator
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextAdded
import java.time.Instant

@Component
class AddTextCommandHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository
) {
    fun execute(command: AddTextCommand) {
        println("Time: ${Instant.now()}. Thread: ${Thread.currentThread().name}. AddTextCommandHandler: $command")
        val collaborativeSession = collaborativeSessionRepository.findById(command.collaborativeSessionId)
        ensureRequesterIsCollaborator(collaborativeSession = collaborativeSession, requesterId = command.requesterId)

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
