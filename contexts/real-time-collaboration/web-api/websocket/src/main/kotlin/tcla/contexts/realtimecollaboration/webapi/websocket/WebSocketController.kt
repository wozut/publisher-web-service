package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

@Controller
class CollaborativeDocumentController(
    private val documentStateService: DocumentStateService,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @SubscribeMapping("/document/{documentId}")
    fun subscribeToDocument(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor
    ): Document {
        println("subscribeToDocument Thread name: ${Thread.currentThread().name}")
        val userId = headerAccessor.sessionAttributes["userId"] as? String
        println("subscribeToDocument userId: ${userId}")
        documentStateService.addCollaborator(documentId, userId!!)
        return documentStateService.getDocument(documentId)
    }

    @MessageMapping("/document/{documentId}/update")
    fun updateDocument(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor,
        documentChange: DocumentChange,
    ) {
        println("update Thread name: ${Thread.currentThread().name}")
        val userId = headerAccessor.sessionAttributes["userId"] as? String
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

    @MessageMapping("/document/{documentId}/join")
    fun joinDocument(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val userId = headerAccessor.sessionAttributes["userId"] as? String
        println("join Thread name: ${Thread.currentThread().name}")
        println("joinDocument requesterId: ${userId}")
        documentStateService.addCollaborator(documentId, userId!!)

        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/collaborators",
            mapOf("action" to "join", "userId" to userId)
        )
    }

    @MessageMapping("/document/{documentId}/leave")
    fun leaveDocument(
        @DestinationVariable documentId: String,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val userId = headerAccessor.sessionAttributes["userId"] as? String
        println("leave Thread name: ${Thread.currentThread().name}")
        documentStateService.removeCollaborator(documentId, userId!!)

        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/collaborators",
            mapOf("action" to "leave", "userId" to userId)
        )
    }
}
