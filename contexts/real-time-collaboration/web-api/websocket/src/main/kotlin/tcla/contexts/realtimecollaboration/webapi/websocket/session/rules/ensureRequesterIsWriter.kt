package tcla.contexts.realtimecollaboration.webapi.websocket.session.rules

import tcla.contexts.realtimecollaboration.webapi.websocket.session.Session
import java.util.UUID

fun ensureRequesterIsWriter(
    session: Session,
    requesterId: UUID
) {
    if(!session.writerExistsByUserId(userId = requesterId)) throw IllegalArgumentException()
}