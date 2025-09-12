package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator.AddCollaboratorToSessionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator.AddCollaboratorToSessionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQuery
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQueryHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.events.*
import java.util.UUID.fromString

@Controller
class CollaborativeDocumentController(
    private val documentStateService: DocumentStateService,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val findCollaborativeSessionByDocumentIdQueryHandler: FindCollaborativeSessionByDocumentIdQueryHandler,
    private val addCollaboratorToSessionCommandHandler: AddCollaboratorToSessionCommandHandler,
) {

    @SubscribeMapping("/updates/{documentId}")
    fun onSubscribeToUpdates(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor,
//        @Payload subscribeToUpdatesRequest: SubscribeToUpdatesRequest
    ): CollaborativeSession {
        val userId = extractUserId(headerAccessor)
        val uuid = fromString(userId!!)
        val documentUuid = fromString(documentId)
        println("onSubscribeToUpdates userId: $uuid")

        val command = AddCollaboratorToSessionCommand(userId = uuid, collaboratorId = uuid, documentId = documentUuid)
        addCollaboratorToSessionCommandHandler.execute(command)
        val query = FindCollaborativeSessionByDocumentIdQuery(documentId = documentUuid)
        val collaborativeSession: CollaborativeSession = findCollaborativeSessionByDocumentIdQueryHandler.execute(query)

        return collaborativeSession
    }

    @MessageMapping("/cursor-position-changed")
    fun cursorPositionChanged(
        headerAccessor: SimpMessageHeaderAccessor,
        cursorPositionChanged: CursorPositionChanged
    ) {
        val userId = extractUserId(headerAccessor)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/${cursorPositionChanged.collaborativeSessionId}/cursor-position-changed",
            cursorPositionChanged.copy(collaboratorId = fromString(userId!!))
        )
    }

    @MessageMapping("/text-added")
    fun textAdded(
        headerAccessor: SimpMessageHeaderAccessor,
        textAdded: TextAdded,
    ) {
        val userId = extractUserId(headerAccessor)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/${textAdded.collaborativeSessionId}/text-added",
            textAdded.copy(collaboratorId = fromString(userId!!))
        )
    }

    @MessageMapping("/text-removed")
    fun textRemoved(
        headerAccessor: SimpMessageHeaderAccessor,
        textRemoved: TextRemoved,
    ) {
        val userId = extractUserId(headerAccessor)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/${textRemoved.collaborativeSessionId}/text-removed",
            textRemoved.copy(collaboratorId = fromString(userId!!))
        )
    }

    @MessageMapping("/text-selected")
    fun textSelected(
        headerAccessor: SimpMessageHeaderAccessor,
        textSelected: TextSelected
    ) {
        val userId = extractUserId(headerAccessor)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/${textSelected.collaborativeSessionId}/text-selected",
            textSelected.copy(collaboratorId = fromString(userId!!))
        )
    }

    @MessageMapping("/text-deselected")
    fun textDeselected(
        headerAccessor: SimpMessageHeaderAccessor,
        textDeselected: TextDeselected
    ) {
        val userId = extractUserId(headerAccessor)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/${textDeselected.collaborativeSessionId}/text-deselected",
            textDeselected.copy(collaboratorId = fromString(userId!!))
        )
    }

    // common logic
    @EventListener
    fun handleSessionConnected(event: SessionConnectedEvent) {
        println("SessionConnectedEvent")
        // Aquí puedes ejecutar lógica cuando se conecta una sesión
    }

    // common logic
    @EventListener  
    fun handleSessionDisconnect(event: SessionDisconnectEvent) {
//        val userId = event.sessionAttributes["userId"] as? String
//        val uuid = userId?.let { fromString(it) }
        
        println("SessionDisconnectEvent: ${event.sessionId}")
    }

    // common logic
    @EventListener
    fun handleSessionSubscribe(event: SessionSubscribeEvent) {
        val destination = event.message.headers["simpDestination"] as? String
//        val userId = event.sessionAttributes["userId"] as? String


        println("SessionSubscribeEvent: $destination")
        // Ejecutar lógica cuando se suscribe a un topic específico
    }

    // common logic
    @EventListener
    fun handleSessionUnsubscribe(event: SessionUnsubscribeEvent) {
        val subscriptionId = event.message.headers["simpSubscriptionId"] as? String
//        val userId = event.sessionAttributes["userId"] as? String
        
        println("SessionUnsubscribeEvent: $subscriptionId")

    }
}

private fun extractUserId(headerAccessor: SimpMessageHeaderAccessor): String? =
    headerAccessor.sessionAttributes["userId"] as? String
