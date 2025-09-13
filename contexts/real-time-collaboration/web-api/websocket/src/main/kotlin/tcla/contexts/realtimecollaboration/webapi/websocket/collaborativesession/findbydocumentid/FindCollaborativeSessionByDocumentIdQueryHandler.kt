package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSessionRepository

@Component
class FindCollaborativeSessionByDocumentIdQueryHandler(
    private val collaborativeSessionRepository: CollaborativeSessionRepository
) {
    fun execute(query: FindCollaborativeSessionByDocumentIdQuery): CollaborativeSession {
        return collaborativeSessionRepository.findByDocumentId(query.documentId)
    }

}