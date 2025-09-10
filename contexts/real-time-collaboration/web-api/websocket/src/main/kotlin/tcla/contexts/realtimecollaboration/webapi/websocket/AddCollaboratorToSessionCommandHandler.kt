package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Component
import java.util.*

@Component
class AddCollaboratorToSessionCommandHandler(
    private val collaborativeSessionStateRepository: CollaborativeSessionStateRepository
) {
    fun execute(collaboratorId: UUID, documentId: UUID) {
        if(!collaborativeSessionStateRepository.existsByDocumentId(documentId)) {
            collaborativeSessionStateRepository.create(
                CollaborativeSessionState(
                    id = UUID.randomUUID(),
                    documentState = DocumentState(documentId = documentId, content = ""),
                    collaboratorStates = setOf()
                )
            )
        }

        val collaborativeSessionState: CollaborativeSessionState = collaborativeSessionStateRepository.findByDocumentId(documentId)
        collaborativeSessionState.addCollaboratorState(CollaboratorState(
            collaboratorId = collaboratorId,
            cursorPosition = 0,
            selectedText = null
        ))
        collaborativeSessionStateRepository.saveChanges(collaborativeSessionState)
    }

}