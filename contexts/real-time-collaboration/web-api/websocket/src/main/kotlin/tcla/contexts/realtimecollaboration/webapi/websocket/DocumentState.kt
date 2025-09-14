package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class DocumentState(val documentId: UUID, val content: String) {
    fun addText(position: Long, text: String): DocumentState {
        return copy(
            content = content.substring(
                startIndex = 0,
                endIndex = position.toInt()
            ) + text + content.substring(
                startIndex = position.toInt()
            )
        )
    }
}
