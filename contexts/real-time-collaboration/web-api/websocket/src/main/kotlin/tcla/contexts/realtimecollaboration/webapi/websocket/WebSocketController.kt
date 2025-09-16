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
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext.RemoveTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext.RemoveTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext.SelectTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext.SelectTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext.DeselectTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext.DeselectTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removecollaborator.RemoveCollaboratorFromSessionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removecollaborator.RemoveCollaboratorFromSessionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPositionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPositionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQuery
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQueryHandler
import java.util.UUID.fromString

@Controller
class CollaborativeDocumentController(
    private val findCollaborativeSessionByDocumentIdQueryHandler: FindCollaborativeSessionByDocumentIdQueryHandler,
    private val addCollaboratorToSessionCommandHandler: AddCollaboratorToSessionCommandHandler,
    private val removeCollaboratorFromSessionCommandHandler: RemoveCollaboratorFromSessionCommandHandler,
    private val changeCursorPositionCommandHandler: ChangeCursorPositionCommandHandler,
    private val addTextCommandHandler: AddTextCommandHandler,
    private val removeTextCommandHandler: RemoveTextCommandHandler,
    private val selectTextCommandHandler: SelectTextCommandHandler,
    private val deselectTextCommandHandler: DeselectTextCommandHandler
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

    @Synchronized
    @MessageMapping("/remove-text")
    fun removeText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload removeTextRequest: RemoveTextRequest,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(removeTextRequest.collaborativeSessionId)
        val collaboratorUuid = fromString(removeTextRequest.collaboratorId)

        val command = RemoveTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            position = removeTextRequest.position,
            length = removeTextRequest.length
        )
        removeTextCommandHandler.execute(command = command)
    }

    @Synchronized
    @MessageMapping("/select-text")
    fun selectText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload selectTextRequest: SelectTextRequest,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(selectTextRequest.collaborativeSessionId)
        val collaboratorUuid = fromString(selectTextRequest.collaboratorId)

        val command = SelectTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            position = selectTextRequest.position,
            length = selectTextRequest.length
        )
        selectTextCommandHandler.execute(command = command)
    }

    @Synchronized
    @MessageMapping("/deselect-text")
    fun deselectText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload deselectTextRequest: DeselectTextRequest,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(deselectTextRequest.collaborativeSessionId)
        val collaboratorUuid = fromString(deselectTextRequest.collaboratorId)

        val command = DeselectTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid
        )
        deselectTextCommandHandler.execute(command = command)
    }

    // common logic
    @EventListener
    fun onSessionConnected(event: SessionConnectedEvent) {
        println("SessionConnectedEvent: ${event.user?.name}")
        // Aquí puedes ejecutar lógica cuando se conecta una sesión
    }

    // common logic
    @EventListener
    fun onSessionDisconnect(event: SessionDisconnectEvent) {
//        val userId = event.sessionAttributes["userId"] as? String
//        val uuid = userId?.let { fromString(it) }

        println("SessionDisconnectEvent: ${event.sessionId}")
    }

    // common logic
    @EventListener
    fun onSessionSubscribe(event: SessionSubscribeEvent) {
        val destination = event.message.headers["simpDestination"] as? String
        println("SessionSubscribeEvent: destination=$destination, user=${event.user?.name}")
        // Ejecutar lógica cuando se suscribe a un topic específico
    }

    // common logic
    @EventListener
    fun onSessionUnsubscribe(event: SessionUnsubscribeEvent) {
        val destination = event.message.headers["simpDestination"] as? String
        val userId = event.user?.name

        println("SessionUnsubscribeEvent: destination=$destination, userId=$userId")

        // Check if unsubscribing from updates destination
        if (destination != null && destination.startsWith("/topic/updates/") && userId != null) {
            val documentId = destination.removePrefix("/topic/updates/")

            val uuid = fromString(userId)
            val documentUuid = fromString(documentId)
            val command = RemoveCollaboratorFromSessionCommand(
                requesterId = uuid,
                collaboratorId = uuid,
                documentId = documentUuid
            )
            removeCollaboratorFromSessionCommandHandler.execute(command)

        }
    }
}

private fun extractRequesterId(headerAccessor: SimpMessageHeaderAccessor): String? =
    headerAccessor.sessionAttributes?.get("userId") as? String
