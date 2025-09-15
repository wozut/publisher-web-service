package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession

import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.DocumentState
import java.util.*

data class CollaborativeSession(
    val id: UUID,
    val documentState: DocumentState,
    val collaboratorStates: MutableSet<CollaboratorState>,
    val lastCollaborativeEventSequenceNumber: Long
) {
    fun addCollaboratorState(collaboratorState: CollaboratorState): CollaborativeSession {
        if (!collaboratorStates.add(collaboratorState)) throw IllegalArgumentException()
        return this
    }

    fun incrementLastCollaborativeEventSequenceNumber(): CollaborativeSession =
        copy(lastCollaborativeEventSequenceNumber = lastCollaborativeEventSequenceNumber + 1)

    fun changeCursorPosition(collaboratorId: UUID, newPosition: Long): CollaborativeSession {
        val collaboratorState: CollaboratorState = collaboratorStates.first { it.collaboratorId == collaboratorId }
        if (!collaboratorStates.remove(collaboratorState)) throw IllegalStateException()
        val updatedCollaboratorState = collaboratorState.changeCursorPosition(newPosition)
        if (!collaboratorStates.add(updatedCollaboratorState)) throw IllegalStateException()
        return this
    }

    fun addText(position: Long, text: String): CollaborativeSession {
        val updatedDocumentState = documentState.addText(position, text)
        return copy(documentState = updatedDocumentState)
    }

    fun removeText(position: Long, length: Long): CollaborativeSession {
        val updatedDocumentState = documentState.removeText(position, length)
        return copy(documentState = updatedDocumentState)
    }
}