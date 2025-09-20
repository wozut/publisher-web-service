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
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.join.JoinSessionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.join.JoinSessionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext.AddTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addtext.AddTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext.RemoveTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removetext.RemoveTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext.SelectTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.selecttext.SelectTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext.DeselectTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.deselecttext.DeselectTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.leave.LeaveSessionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.leave.LeaveSessionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPositionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.changecursorposition.ChangeCursorPositionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQuery
import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.findbydocumentid.FindCollaborativeSessionByDocumentIdQueryHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.AddTextMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.ChangeCursorPositionMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.SelectTextMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.DeselectTextMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.RemoveTextMessage
import java.util.UUID.fromString

@Controller
class CollaborativeSessionController(
    private val findCollaborativeSessionByDocumentIdQueryHandler: FindCollaborativeSessionByDocumentIdQueryHandler,
    private val joinSessionCommandHandler: JoinSessionCommandHandler,
    private val leaveSessionCommandHandler: LeaveSessionCommandHandler,
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
            JoinSessionCommand(requesterId = uuid, documentId = documentUuid)
        joinSessionCommandHandler.execute(command)
        //TODO: aplicar mismo patrón que en CollaborativeSessionController.changeCursorPosition
        val query = FindCollaborativeSessionByDocumentIdQuery(documentId = documentUuid)
        val collaborativeSession: CollaborativeSession = findCollaborativeSessionByDocumentIdQueryHandler.execute(query)

        return collaborativeSession
    }

    @Synchronized
    @MessageMapping("/change-cursor-position")
    fun changeCursorPosition(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload changeCursorPositionMessage: ChangeCursorPositionMessage,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(changeCursorPositionMessage.collaborativeSessionId)
        val collaboratorUuid = fromString(changeCursorPositionMessage.collaboratorId)

        val command = ChangeCursorPositionCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            newPosition = changeCursorPositionMessage.newPosition,
            sequenceNumber = changeCursorPositionMessage.sequenceNumber
        )
        changeCursorPositionCommandHandler.execute(command = command)
    }

    @Synchronized
    @MessageMapping("/add-text")
    fun addText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload addTextMessage: AddTextMessage,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(addTextMessage.collaborativeSessionId)
        val collaboratorUuid = fromString(addTextMessage.collaboratorId)

        val command = AddTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            position = addTextMessage.position,
            text = addTextMessage.text,
            sequenceNumber = addTextMessage.sequenceNumber
        )
        addTextCommandHandler.execute(command = command)
    }

    @Synchronized
    @MessageMapping("/remove-text")
    fun removeText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload removeTextMessage: RemoveTextMessage,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(removeTextMessage.collaborativeSessionId)
        val collaboratorUuid = fromString(removeTextMessage.collaboratorId)

        val command = RemoveTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            position = removeTextMessage.position,
            length = removeTextMessage.length,
            sequenceNumber = removeTextMessage.sequenceNumber
        )
        removeTextCommandHandler.execute(command = command)
    }

    @Synchronized
    @MessageMapping("/select-text")
    fun selectText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload selectTextMessage: SelectTextMessage,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(selectTextMessage.collaborativeSessionId)
        val collaboratorUuid = fromString(selectTextMessage.collaboratorId)

        val command = SelectTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            position = selectTextMessage.position,
            length = selectTextMessage.length,
            sequenceNumber = selectTextMessage.sequenceNumber
        )
        selectTextCommandHandler.execute(command = command)
    }

    @Synchronized
    @MessageMapping("/deselect-text")
    fun deselectText(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload deselectTextMessage: DeselectTextMessage,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val collaborativeSessionUuid = fromString(deselectTextMessage.collaborativeSessionId)
        val collaboratorUuid = fromString(deselectTextMessage.collaboratorId)

        val command = DeselectTextCommand(
            requesterId = requesterUuid,
            collaborativeSessionId = collaborativeSessionUuid,
            collaboratorId = collaboratorUuid,
            sequenceNumber = deselectTextMessage.sequenceNumber
        )
        deselectTextCommandHandler.execute(command = command)
    }

    // common logic
    @EventListener
    fun onSessionConnected(event: SessionConnectedEvent) {
        val headerAccessor: SimpMessageHeaderAccessor = SimpMessageHeaderAccessor.wrap(event.message)
//        val userId: String? = headerAccessor.getNativeHeader("UserId")?.firstOrNull()
//        println("onSessionConnected getNativeHeader(\"UserId\"): $userId")
//        headerAccessor.sessionAttributes["userId"] = userId
//        println("onSessionConnected sessionAttributes userId ${headerAccessor.sessionAttributes["userId"]}")
//        val requesterUuid = fromString(extractRequesterId(headerAccessor))

//        println("SessionConnectedEvent. RequesterId: $requesterUuid")
    }

    // common logic
    @EventListener
    fun onSessionDisconnect(event: SessionDisconnectEvent) {
//        val headerAccessor: SimpMessageHeaderAccessor = SimpMessageHeaderAccessor.wrap(event.message)
//        val requesterUuid = fromString(extractRequesterId(headerAccessor))
//        println("SessionDisconnectEvent. RequesterId: $requesterUuid")
    }

    // common logic
    @EventListener
    fun onSessionSubscribe(event: SessionSubscribeEvent) {
//        val headerAccessor: SimpMessageHeaderAccessor = SimpMessageHeaderAccessor.wrap(event.message)
//        val destination = headerAccessor.destination
//        val requesterUuid = fromString(extractRequesterId(headerAccessor))
//        println("SessionSubscribeEvent: destination=$destination, user=${requesterUuid}")
        // Ejecutar lógica cuando se suscribe a un topic específico
    }

    // common logic
    @EventListener
    fun onSessionUnsubscribe(event: SessionUnsubscribeEvent) {
        val headerAccessor: SimpMessageHeaderAccessor = SimpMessageHeaderAccessor.wrap(event.message)
        val destination = headerAccessor.destination
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        println("SessionUnsubscribeEvent: destination=$destination, requesterUuid=$requesterUuid")

        //TODO: aplicar mismo patrón que en CollaborativeSessionController.changeCursorPosition
        if (destination != null && destination.startsWith("/topic/updates/") && requesterUuid != null) {
            val documentId = destination.removePrefix("/topic/updates/")
            
            val documentUuid = fromString(documentId)
            val command = LeaveSessionCommand(
                requesterId = requesterUuid,
                documentId = documentUuid
            )
            leaveSessionCommandHandler.execute(command)
        }
    }
}
