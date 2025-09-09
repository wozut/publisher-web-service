package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class TextDeselected(val collaborationSessionId: UUID, val collaboratorId: UUID)
