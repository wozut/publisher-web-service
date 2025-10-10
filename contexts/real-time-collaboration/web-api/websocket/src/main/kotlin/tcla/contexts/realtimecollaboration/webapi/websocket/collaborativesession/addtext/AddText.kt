package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.CollaborativeEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.requests.AddTextRequest

@Service
class AddText(
    private val collaborativeSessionRepository: CollaborativeSessionRepository,
    private val collaborativeEventRepository: CollaborativeEventRepository,
) {
    fun execute(request: AddTextRequest) {
        val collaborativeSession = collaborativeSessionRepository.findById(request.collaborativeSessionId)

        var updatedCollaborativeSession = collaborativeSession.addText(
            collaboratorId = request.collaboratorId,
            text = request.text,
            position = request.position
        )

        updatedCollaborativeSession = collaborativeSessionRepository.saveChanges(updatedCollaborativeSession)

        collaborativeEventRepository.createAll(
            collaborativeEvents = updatedCollaborativeSession.popAllGeneratedCollaborativeEvents()
        )
    }
}