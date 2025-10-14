package tcla.contexts.realtimecollaboration.webapi.websocket.session.rules

import tcla.contexts.realtimecollaboration.webapi.websocket.WriterState
import java.util.UUID

fun ensureRequesterOwnsWriterState(
    writerState: WriterState,
    requesterId: UUID
) {
    if (writerState.userId != requesterId) throw IllegalArgumentException()
}