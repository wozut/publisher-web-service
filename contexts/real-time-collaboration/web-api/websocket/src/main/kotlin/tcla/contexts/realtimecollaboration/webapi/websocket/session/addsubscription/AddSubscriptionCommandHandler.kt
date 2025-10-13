package tcla.contexts.realtimecollaboration.webapi.websocket.session.addsubscription

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.Subscription
import tcla.contexts.realtimecollaboration.webapi.websocket.session.CreateSession
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.addnewwriterstate.AddNewWriterStateToSession

@Component
class AddSubscriptionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val createSession: CreateSession,
    private val addNewWriterStateToSession: AddNewWriterStateToSession,
) {
    @Synchronized
    fun execute(command: AddSubscriptionCommand) {
        val startedStatus = Session.Status.STARTED
        val documentId = command.documentId
        var session: Session = if (!sessionRepository.existsByDocumentIdAndStatus(
                documentId = documentId,
                status = startedStatus
            )
        ) {
            val newSession = createSession.execute(documentId = documentId)
            newSession.start()
            sessionRepository.saveChanges(newSession)
        } else sessionRepository.findByDocumentIdAndStatus(documentId, startedStatus)


        val requesterId = command.requesterId
        if (!session.writerExistsByUserId(userId = requesterId)) {
            session = addNewWriterStateToSession.execute(session = session, requesterId = requesterId)
        }

        val writerState = session.findWriterStateByUserId(requesterId)

        val subscription = command.subscription
        session = session.addSubscription(writerState.writerId, subscription)

        session = sessionRepository.saveChanges(session)

        if (subscription.type == Subscription.Type.SESSION) {
            simpMessagingTemplate.convertAndSendToUser(
                writerState.userId.toString(),
                "/queue/session-state/${session.documentState.documentId}",
                session
            )
        }
    }
}