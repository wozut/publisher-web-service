package tcla.contexts.realtimecollaboration.webapi.websocket.events

import java.util.UUID

data class TextDeselected(val collaborativeSessionId: UUID, val collaboratorId: UUID)
