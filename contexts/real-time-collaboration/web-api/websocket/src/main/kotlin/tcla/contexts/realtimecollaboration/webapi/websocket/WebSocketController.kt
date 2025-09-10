package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.DestinationVariable
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

    @SubscribeMapping("/get-updates")
    fun getUpdates(
        headerAccessor: SimpMessageHeaderAccessor,
        getUpdatesRequest: GetUpdatesRequest
    ): CollaborativeSessionState {
        val userId = extractUserId(headerAccessor)
        val uuid = fromString(userId!!)
        println("subscribeToDocument userId: $uuid")
        addCollaboratorToSessionCommandHandler.execute(collaboratorId = uuid, documentId = getUpdatesRequest.documentId)
        val collaborativeSessionState = findCollaborativeSessionStateByDocumentIdQueryHandler.execute(getUpdatesRequest.documentId)

        simpMessagingTemplate.convertAndSend(
            "/topic/collaborator-joined",
            CollaboratorJoined(collaboratorId = userId)
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
            "/topic/document/$documentId/changes",
            changeNotification
        )
    }

    @MessageMapping("/text-added")
    fun textAdded(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor,
        textAdded: TextAdded,
    ) {
        println("update Thread name: ${Thread.currentThread().name}")
        val userId = extractUserId(headerAccessor)
        val updatedDocument = documentStateService.updateDocument(
            documentId,
            documentChange.content,
            userId!!
        )

        val changeNotification = DocumentChange(
            documentId = documentId,
            content = updatedDocument.content,
            userId = userId,
            version = updatedDocument.version
        )

        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/changes",
            changeNotification
        )
    }

    @MessageMapping("/text-removed")
    fun textRemoved(
        headerAccessor: SimpMessageHeaderAccessor,
        textRemoved: TextRemoved,
    ) {
        println("update Thread name: ${Thread.currentThread().name}")
        val userId = extractUserId(headerAccessor)
        val updatedDocument = documentStateService.updateDocument(
            documentId,
            documentChange.content,
            userId!!
        )

        val changeNotification = DocumentChange(
            documentId = documentId,
            content = updatedDocument.content,
            userId = userId,
            version = updatedDocument.version
        )

        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/changes",
            changeNotification
        )
    }

    @MessageMapping("/text-selected")
    fun textDeselected(
        headerAccessor: SimpMessageHeaderAccessor,
        textSelected: TextSelected
    ) {

        val userId = extractUserId(headerAccessor)


        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/changes",
            changeNotification
        )
    }

    @MessageMapping("/text-deselected")
    fun textDeselected(
        headerAccessor: SimpMessageHeaderAccessor,
        textDeselected: TextDeselected
    ) {

        val userId = extractUserId(headerAccessor)


        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/changes",
            changeNotification
        )
    }
}

private fun extractUserId(headerAccessor: SimpMessageHeaderAccessor): String? =
    headerAccessor.sessionAttributes["userId"] as? String
