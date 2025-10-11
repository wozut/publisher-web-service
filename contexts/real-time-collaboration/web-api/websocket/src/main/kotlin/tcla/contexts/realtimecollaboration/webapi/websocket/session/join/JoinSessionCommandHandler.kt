package tcla.contexts.realtimecollaboration.webapi.websocket.session.join

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.WriterState
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.CreateSession
import tcla.contexts.realtimecollaboration.webapi.websocket.events.WriterJoined
import java.util.UUID

@Component
class JoinSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository,
    private val createSession: CreateSession
) {
    fun execute(command: JoinSessionCommand) {
        if (!sessionRepository.existsByDocumentId(documentId = command.documentId)) {
            createSession.execute(documentId = command.documentId)
        }

        //TODO: aplicar mismo patrón que en ChangeCursorPositionCommandHandler
        val session: Session =
            sessionRepository.findByDocumentId(command.documentId)

        //TODO: fallar si ya existe?
        if(session.writerExistsByUserId(userId = command.requesterId)) return

        val writerId = UUID.randomUUID()
        val writerState = WriterState(
            userId = command.requesterId,
            writerId = writerId,
            cursorPosition = null,
            selectedText = null
        )

        var updatedSession = session
            .addWriterState(writerState)

        updatedSession = sessionRepository.saveChanges(updatedSession)

        val writerJoined = WriterJoined(
            writerId = writerId,
            sessionId = updatedSession.id,
            sequenceNumber = updatedSession.lastSessionEventSequenceNumber,
            broadcasted = false,
        )
        sessionEventRepository.create(writerJoined)
    }

}