package tcla.contexts.realtimecollaboration.webapi.websocket

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