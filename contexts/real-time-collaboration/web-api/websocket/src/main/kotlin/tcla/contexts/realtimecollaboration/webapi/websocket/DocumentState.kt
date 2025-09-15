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

    fun removeText(position: Long, length: Long): DocumentState {
        require(position >= 0) { "Position must be non-negative, got: $position" }
        require(length > 0) { "Length must be positive, got: $length" }
        require(position < content.length) { "Position $position is invalid. Document length: ${content.length}" }
        require(position + length <= content.length) { "Remove range [$position, ${position + length}) extends beyond document length ${content.length}" }

        val positionInt = position.toInt()
        val endPositionInt = (position + length).toInt()
        return copy(
            content = content.substring(0, positionInt) + content.substring(endPositionInt)
        )
    }
}
