package tcla.contexts.realtimecollaboration.webapi.websocket.session.removesubscription

import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.removewriterstate.RemoveWriterStateFromSession

@Component
class RemoveSubscriptionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val removeWriterStateFromSession: RemoveWriterStateFromSession,
) {
    fun execute(command: RemoveSubscriptionCommand) {
        if (!sessionRepository.existsByDocumentIdAndStatus(documentId = command.documentId, status = Session.Status.STARTED)) {
            throw IllegalArgumentException("Session not found for document: ${command.documentId}")
        }

        var session: Session =
            sessionRepository.findByDocumentIdAndStatus(command.documentId, Session.Status.STARTED)

        var writerState = session.findWriterStateByUserId(command.requesterId)

        session = session.removeSubscription(writerId = writerState.writerId, subscriptionId = command.subscriptionId)

        writerState = session.findWriterStateByUserId(command.requesterId)
        if(!writerState.hasSubscriptions()) {
            session = removeWriterStateFromSession.execute(session, command.requesterId)
        }

        if(!session.hasWriters()) {
            session = session.end()
        }

        sessionRepository.saveChanges(session)
    }

}