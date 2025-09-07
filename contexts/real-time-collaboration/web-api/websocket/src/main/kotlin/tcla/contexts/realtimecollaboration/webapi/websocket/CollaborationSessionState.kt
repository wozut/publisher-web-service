package tcla.contexts.realtimecollaboration.webapi.websocket

data class CollaborationSessionState(val document: Document, val collaboratorStates: Set<CollaboratorState>)