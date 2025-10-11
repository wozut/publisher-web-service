package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent
import tcla.contexts.realtimecollaboration.webapi.websocket.session.join.JoinSessionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.session.join.JoinSessionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.session.addtext.AddTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.session.addtext.AddTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.session.removetext.RemoveTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.session.removetext.RemoveTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.session.selecttext.SelectTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.session.selecttext.SelectTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.session.deselecttext.DeselectTextCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.session.deselecttext.DeselectTextCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.session.leave.LeaveSessionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.session.leave.LeaveSessionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.session.changecursorposition.ChangeCursorPositionCommand
import tcla.contexts.realtimecollaboration.webapi.websocket.session.changecursorposition.ChangeCursorPositionCommandHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.session.findbydocumentid.FindSessionByDocumentIdQueryHandler
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.AddTextMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.ChangeCursorPositionMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.SelectTextMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.DeselectTextMessage
import tcla.contexts.realtimecollaboration.webapi.websocket.messages.RemoveTextMessage
import java.util.UUID.fromString

@Controller
class SessionController(
    private val findSessionByDocumentIdQueryHandler: FindSessionByDocumentIdQueryHandler,
    private val joinSessionCommandHandler: JoinSessionCommandHandler,
    private val leaveSessionCommandHandler: LeaveSessionCommandHandler,
    private val changeCursorPositionCommandHandler: ChangeCursorPositionCommandHandler,
    private val addTextCommandHandler: AddTextCommandHandler,
    private val removeTextCommandHandler: RemoveTextCommandHandler,
    private val selectTextCommandHandler: SelectTextCommandHandler,
    private val deselectTextCommandHandler: DeselectTextCommandHandler
) {

    //TODO: change to "/updates/{collaborativeSessionId}"
    /*TODO: no funciona porque el cliente no se esta suscribiendo a /topic/updates/{documentId}
     *  sino a /app/updates/{documentId}
     * Posibles Soluciones:
     * - Dos suscripciones diferentes: /topic/updates/{documentId} y /app/updates/{documentId}
     * - (Mejor)
     *      - un mapping (MessageMapping|SendToUser) para que los clientes pidan el estado actual de la sesión
     *      - @SubscribeMapping("/updates/{documentId}") -> desaparece
     */
/*    @Synchronized
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
    }*/

    // 1. (client) subscribe /topic/updates/{documentId}
    // 2. (client) subscribe [/user]/queue/collaborative-session-state/{documentId}
    // 3. (client) send      /app/join-session/{documentId}
    // 4. (server) send      /user/{username}/queue/collaborative-session-state/{documentId}
    // 5. (server) send      /topic/updates/{documentId}

    //TODO: separar este flujo en 2: joinSession y getCollaborativeSessionState
    @Synchronized
    @MessageMapping("/join-session/{documentId}")
    fun joinSession(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor,
    ) {
        val requesterId = extractRequesterId(headerAccessor)
        val uuid = fromString(requesterId!!)

        val documentUuid = fromString(documentId)
        println("joinSession requesterId: $uuid")

        val command =
            JoinSessionCommand(requesterId = uuid, documentId = documentUuid)
        joinSessionCommandHandler.execute(command)
    }

    @Synchronized
    @MessageMapping("/change-cursor-position")
    fun changeCursorPosition(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload changeCursorPositionMessage: ChangeCursorPositionMessage,
    ) {
        val requesterUuid = fromString(extractRequesterId(headerAccessor))
        val sessionUuid = fromString(changeCursorPositionMessage.sessionId)
        val writerUuid = fromString(changeCursorPositionMessage.writerId)

        val command = ChangeCursorPositionCommand(
            requesterId = requesterUuid,
            sessionId = sessionUuid,
            writerId = writerUuid,
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
        val sessionUuid = fromString(addTextMessage.sessionId)
        val writerUuid = fromString(addTextMessage.writerId)

        val command = AddTextCommand(
            requesterId = requesterUuid,
            sessionId = sessionUuid,
            writerId = writerUuid,
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
        val sessionUuid = fromString(removeTextMessage.sessionId)
        val writerUuid = fromString(removeTextMessage.writerId)

        val command = RemoveTextCommand(
            requesterId = requesterUuid,
            sessionId = sessionUuid,
            writerId = writerUuid,
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
        val sessionUuid = fromString(selectTextMessage.sessionId)
        val writerUuid = fromString(selectTextMessage.writerId)

        val command = SelectTextCommand(
            requesterId = requesterUuid,
            sessionId = sessionUuid,
            writerId = writerUuid,
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
        val sessionUuid = fromString(deselectTextMessage.sessionId)
        val writerUuid = fromString(deselectTextMessage.writerId)

        val command = DeselectTextCommand(
            requesterId = requesterUuid,
            sessionId = sessionUuid,
            writerId = writerUuid,
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
        // TODO: guardar suscripciones en CollaborativeSession
    }

    // common logic
    @EventListener
    fun onSessionUnsubscribe(event: SessionUnsubscribeEvent) {
        // TODO: borrar suscripciones de CollaborativeSession
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
