package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class DocumentState(val documentId: UUID, val content: String) {
    fun addText(position: Long, text: String): DocumentState {
        require(position >= 0) { "Position must be non-negative, got: $position" }
        require(position <= content.length) { "Position $position is beyond document length ${content.length}" }

        val positionInt = position.toInt()
        return copy(
            content = content.substring(0, positionInt) + text + content.substring(positionInt)
        )
    }
}
