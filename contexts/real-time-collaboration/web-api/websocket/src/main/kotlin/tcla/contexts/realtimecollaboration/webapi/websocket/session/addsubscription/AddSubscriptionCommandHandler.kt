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
    fun execute(command: AddSubscriptionCommand) {
        //TODO: Check if a started session exists
        if (!sessionRepository.existsByDocumentId(documentId = command.documentId)) {
            createSession.execute(documentId = command.documentId)
        }

        var session: Session = sessionRepository.findByDocumentId(command.documentId)
        //TODO: start session if not started

        //TODO: find started session

        if(!session.writerExistsByUserId(userId = command.requesterId)) {
            session = addNewWriterStateToSession.execute(session = session, requesterId = command.requesterId)
        }

        val writerState = session.findWriterStateByUserId(command.requesterId)

        session = session.addSubscription(writerState.writerId, command.subscription)

        session = sessionRepository.saveChanges(session)

        if(command.subscription.type == Subscription.Type.SESSION) {
            simpMessagingTemplate.convertAndSendToUser(
                writerState.userId.toString(),
                "/queue/session-state/${session.documentState.documentId}",
                session
            )
        }
    }
}