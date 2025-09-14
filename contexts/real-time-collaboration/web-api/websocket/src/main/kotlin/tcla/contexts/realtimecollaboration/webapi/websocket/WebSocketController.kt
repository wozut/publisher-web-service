package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator.AddCollaboratorToSessionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator.AddCollaboratorToSessionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext.AddTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext.AddTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPositionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPositionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQuery
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQueryHandler
import java.util.UUID.fromString

@Controller
class CollaborativeDocumentController(
    private val findCollaborativeSessionByDocumentIdQueryHandler: FindCollaborativeSessionByDocumentIdQueryHandler,
    private val addCollaboratorToSessionCommandHandler: AddCollaboratorToSessionCommandHandler,
    private val changeCursorPositionCommandHandler: ChangeCursorPositionCommandHandler,
    private val addTextCommandHandler: AddTextCommandHandler
) {

    //TODO: change to "/updates/{collaborativeSessionId}"
    @Synchronized
    @SubscribeMapping("/updates/{documentId}")
    fun onSubscribeToUpdates(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor,
    ): CollaborativeSession {
        val requesterId = extractRequesterId(headerAccessor)
        val uuid = fromString(requesterId!!)

        val documentUuid = fromString(documentId)
        println("onSubscribeToUpdates requesterId: $uuid")

        val command =
            AddCollaboratorToSessionCommand(requesterId = uuid, collaboratorId = uuid, documentId = documentUuid)
        addCollaboratorToSessionCommandHandler.execute(command)
        val query = FindCollaborativeSessionByDocumentIdQuery(documentId = documentUuid)
        val collaborativeSession: CollaborativeSession = findCollaborativeSessionByDocumentIdQueryHandler.execute(query)

        return collaborativeSession
    }

    @Synchronized
    @MessageMapping("/change-cursor-position")
    fun changeCursorPosition(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload changeCursorPositionRequest: ChangeCursorPositionRequest,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(changeCursorPositionRequest.collaborativeSessionId)
        val collaboratorUuid = fromString(changeCursorPositionRequest.collaboratorId)

        val command = ChangeCursorPositionCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            newPosition = changeCursorPositionRequest.newPosition
        )
        changeCursorPositionCommandHandler.execute(command = command)
    }

    @Synchronized
    @MessageMapping("/add-text")
    fun addText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload addTextRequest: AddTextRequest,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(addTextRequest.collaborativeSessionId)
        val collaboratorUuid = fromString(addTextRequest.collaboratorId)

        val command = AddTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            position = addTextRequest.position,
            text = addTextRequest.text
        )
        addTextCommandHandler.execute(command = command)
    }

    @MessageMapping("/remove-text")
    fun removeText(
        headerAccessor: SimpMessageHeaderAccessor,
    ) {
        val requesterId = extractRequesterId(headerAccessor)

    }

    @MessageMapping("/select-text")
    fun selectText(
        headerAccessor: SimpMessageHeaderAccessor,
    ) {
        val requesterId = extractRequesterId(headerAccessor)


    }

    @MessageMapping("/deselect-text")
    fun deselectText(
        headerAccessor: SimpMessageHeaderAccessor,
    ) {
        val requesterId = extractRequesterId(headerAccessor)

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

private fun extractRequesterId(headerAccessor: SimpMessageHeaderAccessor): String? =
    headerAccessor.sessionAttributes["userId"] as? String
