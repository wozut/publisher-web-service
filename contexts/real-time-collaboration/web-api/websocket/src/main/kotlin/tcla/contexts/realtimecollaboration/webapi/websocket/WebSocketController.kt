package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.util.UUID.fromString

@Controller
class CollaborativeDocumentController(
    private val documentStateService: DocumentStateService,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val findCollaborativeSessionStateByDocumentIdQueryHandler: FindCollaborativeSessionStateByDocumentIdQueryHandler,
    private val addCollaboratorToSessionCommandHandler: AddCollaboratorToSessionCommandHandler,
) {

    @ConnectMapping
    fun onConnect(
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val userId = extractUserId(headerAccessor)
        println("onConnect. userId: $userId")
    }

    @SubscribeMapping("/updates")
    fun onSubscribeToUpdates(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload getUpdatesRequest: GetUpdatesRequest
    ): CollaborativeSessionState {
        val userId = extractUserId(headerAccessor)
        val uuid = fromString(userId!!)
        println("subscribeToDocument userId: $uuid")
        addCollaboratorToSessionCommandHandler.execute(collaboratorId = uuid, documentId = getUpdatesRequest.documentId)
        val collaborativeSessionState: CollaborativeSessionState = findCollaborativeSessionStateByDocumentIdQueryHandler.execute(getUpdatesRequest.documentId)

        simpMessagingTemplate.convertAndSend(
            "/topic/collaborator-joined",
            CollaboratorJoined(collaboratorId = uuid)
        )
        return collaborativeSessionState
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
}

private fun extractUserId(headerAccessor: SimpMessageHeaderAccessor): String? =
    headerAccessor.sessionAttributes["userId"] as? String
