package tcla.contexts.realtimecollaboration.webapi.websocket.session

import org.springframework.stereotype.Service
import tcla.contexts.realtimecollaboration.webapi.websocket.DocumentState
import java.util.UUID

@Service
class CreateSession(
    private val sessionRepository: SessionRepository
) {
    fun execute(documentId: UUID): Session {
        val session = Session(
            id = UUID.randomUUID(),
            documentState = DocumentState(documentId = documentId, content = ""),
            writerStates = mutableSetOf(),
            lastSessionEventSequenceNumber = -1L,
            status = Session.Status.NOT_STARTED
        )
        return sessionRepository.create(session)
    }
}