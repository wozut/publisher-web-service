package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.rules

import tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.CollaborativeSession
import java.util.UUID

fun ensureRequesterIsCollaborator(
    collaborativeSession: CollaborativeSession,
    requesterId: UUID
) {
    if(!collaborativeSession.collaboratorExistsByUserId(userId = requesterId)) throw IllegalArgumentException()
}