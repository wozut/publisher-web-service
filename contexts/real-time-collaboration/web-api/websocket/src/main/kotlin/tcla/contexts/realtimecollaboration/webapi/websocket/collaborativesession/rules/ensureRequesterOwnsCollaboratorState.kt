package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules

import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import java.util.UUID

fun ensureRequesterOwnsCollaboratorState(
    collaboratorState: CollaboratorState,
    requesterId: UUID
) {
    if (collaboratorState.userId != requesterId) throw IllegalArgumentException()
}