package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class CollaborativeSessionState(val id: UUID, val documentState: DocumentState, val collaboratorStates: Set<CollaboratorState>) {
    fun addCollaboratorState(collaboratorState: CollaboratorState) = copy(collaboratorStates = collaboratorStates + collaboratorState)
}