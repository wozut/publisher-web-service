package tcla.contexts.realtimecollaboration.webapi.websocket.session.addsubscription

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import tcla.contexts.realtimecollaboration.webapi.websocket.Subscription
import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import tcla.contexts.realtimecollaboration.webapi.websocket.session.SessionRepository
import tcla.contexts.realtimecollaboration.webapi.websocket.session.rules.ensureRequesterIsWriterInSession

@Component
class AddSubscriptionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    fun execute(command: AddSubscriptionCommand) {
        val session: Session = sessionRepository.findByDocumentId(command.documentId)
        ensureRequesterIsWriterInSession(session = session, requesterId = command.requesterId)

        val writerState = session.findWriterStateByUserId(command.requesterId)

        var updatedSession = session.addSubscription(writerState.writerId, command.subscription)

        updatedSession = sessionRepository.saveChanges(updatedSession)

        if(command.subscription.type == Subscription.Type.SESSION) {
            simpMessagingTemplate.convertAndSendToUser(
                writerState.userId.toString(),
                "/queue/session-state/${updatedSession.documentState.documentId}",
                updatedSession
            )
        }
    }
}