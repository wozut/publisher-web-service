package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession.addcollaborator

import java.util.UUID

data class AddCollaboratorToSessionCommand(val requesterId: UUID, val collaboratorId: UUID, val documentId: UUID)