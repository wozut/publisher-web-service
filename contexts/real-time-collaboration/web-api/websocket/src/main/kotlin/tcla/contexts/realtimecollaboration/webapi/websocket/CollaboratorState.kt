package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class CollaboratorState(val collaboratorId: UUID, val cursorPosition: Long, val selectedFragment: SelectedFragment?)
