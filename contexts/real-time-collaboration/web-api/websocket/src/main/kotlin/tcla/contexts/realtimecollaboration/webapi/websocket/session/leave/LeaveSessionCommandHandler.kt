package tcla.contexts.realtimecollaboration.webapi.websocket.session.leave

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.SessionEventRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.events.WriterLeft

@Component
class LeaveSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val sessionEventRepository: SessionEventRepository
) {
    fun execute(command: LeaveSessionCommand) {
        if (!sessionRepository.existsByDocumentId(documentId = command.documentId)) {
            throw IllegalArgumentException("Session not found for document: ${command.documentId}")
        }

        val session: Session =
            sessionRepository.findByDocumentId(command.documentId)

        val writerState = session.findWriterStateByUserId(command.requesterId)

        var updatedSession = session
            .removeWriterState(command.requesterId)

        updatedSession = sessionRepository.saveChanges(updatedSession)

        sessionEventRepository.create(
            WriterLeft(
                writerId = writerState.writerId,
                sessionId = updatedSession.id,
                sequenceNumber = updatedSession.lastSessionEventSequenceNumber,
                broadcasted = false,
            )
        )
    }

}