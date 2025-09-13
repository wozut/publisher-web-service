package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession

import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.DocumentState
import java.util.*

data class CollaborativeSession(
    val id: UUID,
    val documentState: DocumentState,
    val collaboratorStates: Set<CollaboratorState>,
    val lastCollaborativeEventSequenceNumber: Long
) {
    fun addCollaboratorState(collaboratorState: CollaboratorState) = copy(collaboratorStates = collaboratorStates + collaboratorState)
    fun setLastCollaborativeEventSequenceNumber(sequenceNumber: Long) = copy(lastCollaborativeEventSequenceNumber = sequenceNumber)
}