package tcla.contexts.realtimecollaboration.webapi.websocket

import java.util.UUID

data class WriterState(
    val userId: UUID,
    val writerId: UUID,
    val subscriptions: MutableSet<Subscription> = mutableSetOf(),
    val cursorPosition: Long?,
    val selectedText: SelectedText?
) {
    fun changeCursorPosition(newPosition: Long) = copy(cursorPosition = newPosition)

    fun selectText(position: Long, length: Long) = copy(selectedText = SelectedText(position, length))

    fun deselectText() = copy(selectedText = null)

    fun hasSubscriptions() = !subscriptions.isEmpty()
}
