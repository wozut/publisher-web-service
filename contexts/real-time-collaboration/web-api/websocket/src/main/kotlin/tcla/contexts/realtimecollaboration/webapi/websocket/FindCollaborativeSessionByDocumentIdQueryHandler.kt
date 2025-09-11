package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Component
import java.util.*

@Component
class FindCollaborativeSessionByDocumentIdQueryHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository
) {
    fun execute(documentId: UUID): CollaborativeSession {
        return collaborativeSessionRepository.findByDocumentId(documentId)
    }

}