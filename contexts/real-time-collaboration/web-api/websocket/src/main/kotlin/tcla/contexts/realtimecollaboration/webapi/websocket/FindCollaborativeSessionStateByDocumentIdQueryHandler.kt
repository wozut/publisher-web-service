package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Component
import java.util.*

@Component
class FindCollaborativeSessionStateByDocumentIdQueryHandler(
    private val collaborativeSessionStateRepository: CollaborativeSessionStateRepository
) {
    fun execute(documentId: UUID): CollaborativeSessionState {
        return collaborativeSessionStateRepository.findByDocumentId(documentId)
    }

}