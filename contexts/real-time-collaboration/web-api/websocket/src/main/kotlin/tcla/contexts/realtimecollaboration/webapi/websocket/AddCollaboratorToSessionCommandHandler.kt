package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Component
import java.util.*

@Component
class AddCollaboratorToSessionCommandHandler(
    private val collaborationSessionStateRepository: CollaborationSessionStateRepository
) {
    fun execute(collaboratorId: UUID, documentId: UUID) {
        if(!collaborationSessionStateRepository.existsByDocumentId(documentId)) {
            collaborationSessionStateRepository.create(
                CollaborationSessionState(
                    id = UUID.randomUUID(),
                    documentState = DocumentState(documentId = documentId, content = ""),
                    collaboratorStates = setOf()
                )
            )
        }

        val collaborationSessionState: CollaborationSessionState = collaborationSessionStateRepository.findByDocumentId(documentId)
        collaborationSessionState.addCollaboratorState(CollaboratorState(
            collaboratorId = collaboratorId,
            cursorPosition = 0,
            selectedText = null
        ))
        collaborationSessionStateRepository.saveChanges(collaborationSessionState)
    }

}