package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

data class Document(
    val id: String,
    var content: String,
    val collaborators: MutableSet<String> = mutableSetOf(),
    var version: Long = 0L
)

data class DocumentChange(
    val documentId: String,
    val content: String,
    val userId: String,
    val version: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Service
class DocumentStateService {
    private val documents = ConcurrentHashMap<String, Document>()
    
    fun getDocument(documentId: String): Document {
        return documents.getOrPut(documentId) {
            Document(
                id = documentId,
                content = "# Welcome to Collaborative Document\n\nStart typing to collaborate in real-time!"
            )
        }
    }
    
    fun updateDocument(documentId: String, content: String, userId: String): Document {
        val document = getDocument(documentId)
        synchronized(document) {
            document.content = content
            document.version++
            document.collaborators.add(userId)
        }
        return document
    }
    
    fun addCollaborator(documentId: String, userId: String) {
        val document = getDocument(documentId)
        document.collaborators.add(userId)
    }
    
    fun removeCollaborator(documentId: String, userId: String) {
        documents[documentId]?.collaborators?.remove(userId)
    }
    
    fun getDocumentVersion(documentId: String): Long {
        return documents[documentId]?.version ?: 0L
    }
}