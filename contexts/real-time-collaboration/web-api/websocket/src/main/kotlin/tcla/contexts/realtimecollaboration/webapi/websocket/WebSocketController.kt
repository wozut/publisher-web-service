package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import tcla.contexts.authentication.core.RequestInfo

@Controller
class CollaborativeDocumentController(
    private val documentStateService: DocumentStateService,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @SubscribeMapping("/document/{documentId}")
    fun subscribeToDocument(
        @DestinationVariable documentId: String
    ): Document {
        val userId = RequestInfo.getRequesterId()!!
        documentStateService.addCollaborator(documentId, userId)
        return documentStateService.getDocument(documentId)
    }

    @MessageMapping("/document/{documentId}/update")
    fun updateDocument(
        @DestinationVariable documentId: String,
        documentChange: DocumentChange,
    ) {
        val updatedDocument = documentStateService.updateDocument(
            documentId, 
            documentChange.content,
            RequestInfo.getRequesterId()!!
        )
        
        val changeNotification = DocumentChange(
            documentId = documentId,
            content = updatedDocument.content,
            userId = RequestInfo.getRequesterId()!!,
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
    ) {
        documentStateService.addCollaborator(documentId, RequestInfo.getRequesterId()!!)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/collaborators",
            mapOf("action" to "join", "userId" to RequestInfo.getRequesterId()!!)
        )
    }
    
    @MessageMapping("/document/{documentId}/leave")
    fun leaveDocument(
        @DestinationVariable documentId: String,
    ) {
        documentStateService.removeCollaborator(documentId, RequestInfo.getRequesterId()!!)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/collaborators",
            mapOf("action" to "leave", "userId" to RequestInfo.getRequesterId()!!)
        )
    }
}
