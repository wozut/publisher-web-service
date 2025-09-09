package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Component
import java.util.*

@Component
class FindCollaborationSessionStateByDocumentIdQueryHandler(
    private val collaborationSessionStateRepository: CollaborationSessionStateRepository
) {
    fun execute(documentId: UUID): CollaborationSessionState {
        return collaborationSessionStateRepository.findByDocumentId(documentId)
    }

}