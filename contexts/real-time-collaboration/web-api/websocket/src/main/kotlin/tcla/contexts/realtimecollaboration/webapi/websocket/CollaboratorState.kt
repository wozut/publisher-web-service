package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class CollaboratorState(val userId: UUID, val collaboratorId: UUID, val cursorPosition: Long?, val selectedText: SelectedText?)
