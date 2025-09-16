package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.removecollaborator

import java.util.UUID

data class RemoveCollaboratorFromSessionCommand(val requesterId: UUID, val collaboratorId: UUID, val documentId: UUID)