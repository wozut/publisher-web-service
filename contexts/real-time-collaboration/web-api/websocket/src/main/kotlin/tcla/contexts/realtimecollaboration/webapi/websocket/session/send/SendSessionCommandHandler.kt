package tcla.contexts.realtimecollaboration.webapi.websocket.session.send

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterIsWriter

@Component
class SendSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    fun handle(command: SendSessionCommand) {
        val session = sessionRepository.findByDocumentId(command.documentId)
        ensureRequesterIsWriter(session = session, requesterId = command.requesterId)
        val writerState = session.findWriterStateByUserId(userId = command.requesterId)
        simpMessagingTemplate.convertAndSendToUser(
            writerState.userId.toString(),
            "/queue/session-state/${session.documentState.documentId}",
            session
        )
    }
}