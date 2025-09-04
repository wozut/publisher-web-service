package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class CollaborativeDocumentController(
    private val documentStateService: DocumentStateService,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @SubscribeMapping("/document/{documentId}")
    fun subscribeToDocument(
        @DestinationVariable documentId: String,
        principal: Principal
    ): Document {
        val userId = principal.name
        documentStateService.addCollaborator(documentId, userId)
        return documentStateService.getDocument(documentId)
    }

    @MessageMapping("/document/{documentId}/update")
    fun updateDocument(
        @DestinationVariable documentId: String,
        documentChange: DocumentChange,
        principal: Principal
    ) {
        val userId = principal.name
        val updatedDocument = documentStateService.updateDocument(
            documentId, 
            documentChange.content, 
            userId
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
        principal: Principal
    ) {
        val userId = principal.name
        documentStateService.addCollaborator(documentId, userId)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/collaborators",
            mapOf("action" to "join", "userId" to userId)
        )
    }
    
    @MessageMapping("/document/{documentId}/leave")
    fun leaveDocument(
        @DestinationVariable documentId: String,
        principal: Principal
    ) {
        val userId = principal.name
        documentStateService.removeCollaborator(documentId, userId)
        
        simpMessagingTemplate.convertAndSend(
            "/topic/document/$documentId/collaborators",
            mapOf("action" to "leave", "userId" to userId)
        )
    }
}
