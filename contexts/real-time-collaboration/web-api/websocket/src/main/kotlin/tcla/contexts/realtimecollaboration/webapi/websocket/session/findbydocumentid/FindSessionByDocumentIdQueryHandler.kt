package tcla.contexts.realtimecollaboration.webapi.websocket.session.findbydocumentid

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository

@Component
class FindSessionByDocumentIdQueryHandler(
    private val sessionRepository: SessionRepository
) {
    fun execute(query: FindSessionByDocumentIdQuery): Session {
        return sessionRepository.findByDocumentId(query.documentId)
    }

}