package tcla.contexts.realtimecollaboration.webapi.websocket.session.send

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository

@Component
class SendSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    fun handle(command: SendSessionCommand) {
        //TODO ensure requester is writer in active session in the document

        val session = sessionRepository.findByDocumentId(command.documentId)
        val writerState = session.findWriterStateByUserId(userId = command.requesterId)
        simpMessagingTemplate.convertAndSendToUser(
            writerState.userId.toString(),
            "/queue/session-state/${session.documentState.documentId}",
            session
        )
    }
}