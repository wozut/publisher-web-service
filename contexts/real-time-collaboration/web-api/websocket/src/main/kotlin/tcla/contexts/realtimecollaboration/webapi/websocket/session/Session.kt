package tcla.contexts.realtimecollaboration.webapi.websocket.session

import tcla.contexts.realtimecollaboration.webapi.websocket.WriterState
import tcla.contexts.realtimecollaboration.webapi.websocket.DocumentState
import tcla.contexts.realtimecollaboration.webapi.websocket.SelectedText
import tcla.contexts.realtimecollaboration.webapi.websocket.Subscription
import tcla.contexts.realtimecollaboration.webapi.websocket.events.SessionEvent
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CursorPositionChanged
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextAdded
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextRemoved
import tcla.contexts.realtimecollaboration.webapi.websocket.events.WriterJoined
import tcla.contexts.realtimecollaboration.webapi.websocket.events.WriterLeft
import java.util.*
import kotlin.collections.toList

data class Session(
    val id: UUID,
    val documentState: DocumentState,
    val writerStates: MutableSet<WriterState>,
    val lastSessionEventSequenceNumber: Long,
    val generatedSessionEvents: MutableList<SessionEvent> = mutableListOf(),
    val status: Status = Status.NOT_STARTED
) : SessionEventGenerator {

    enum class Status {
        NOT_STARTED,
        STARTED,
        ENDED
    }

    private fun nextSessionEventSequenceNumber(): Long = lastSessionEventSequenceNumber + 1

    fun addWriterState(writerState: WriterState): Session {
        println("Adding writerState: $writerState")
        if (writerStates.any { it.userId == writerState.userId }) throw IllegalArgumentException()
        if (writerState.cursorPosition != null) ensureCursorPositionConsistency(writerState.cursorPosition)
        if (writerState.selectedText != null) ensureSelectedTextConsistency(writerState.selectedText)
        if (!writerStates.add(writerState)) throw IllegalArgumentException()
        val writerJoined = WriterJoined(
            writerId = writerState.writerId,
            sessionId = id,
            sequenceNumber = nextSessionEventSequenceNumber(),
            broadcasted = false,
        )
        generatedSessionEvents.add(writerJoined)
        return copyWithEventSequenceNumberIncremented()
    }

    fun removeWriterState(userId: UUID): Session {
        val writerState: WriterState = writerStates.firstOrNull { it.userId == userId }
            ?: throw IllegalArgumentException("Writer not found. UserId: $userId")
        if (!writerStates.remove(writerState)) throw IllegalStateException()
        val writerLeft = WriterLeft(
            writerId = writerState.writerId,
            sessionId = id,
            sequenceNumber = nextSessionEventSequenceNumber(),
            broadcasted = false,
        )
        generatedSessionEvents.add(writerLeft)
        return copyWithEventSequenceNumberIncremented()
    }

    fun changeCursorPosition(collaboratorId: UUID, newPosition: Long): Session {
        val writerState: WriterState = writerStates.first { it.writerId == collaboratorId }
        ensureCursorPositionConsistency(newPosition)
        if (!writerStates.remove(writerState)) throw IllegalStateException()
        val updatedWriterState = writerState.changeCursorPosition(newPosition)
        if (!writerStates.add(updatedWriterState)) throw IllegalStateException()

        val cursorPositionChanged = CursorPositionChanged(
            sessionId = id,
            writerId = collaboratorId,
            sequenceNumber = nextSessionEventSequenceNumber(),
            broadcasted = false,
            newPosition = newPosition
        )

        generatedSessionEvents.add(cursorPositionChanged)

        return copyWithEventSequenceNumberIncremented()
    }

    fun addText(writerId: UUID, position: Long, text: String): Session {
        val updatedDocumentState = documentState.addText(position, text)
        val textAdded = TextAdded(
            sessionId = id,
            writerId = writerId,
            sequenceNumber = nextSessionEventSequenceNumber(),
            broadcasted = false,
            position = position,
            text = text
        )

        generatedSessionEvents.add(textAdded)

        updateAllCursorPositionsAfterTextAdded(position, text.length.toLong())

        return copy(
            documentState = updatedDocumentState
        ).copyWithEventSequenceNumberIncremented()
    }

    private fun updateAllCursorPositionsAfterTextAdded(position: Long, length: Long) {
        writerStates.filter {
            if (it.cursorPosition == null) false
            else it.cursorPosition >= position
        }.forEach { writerState ->
            writerState.changeCursorPosition(writerState.cursorPosition!! + length)
        }
    }

    private fun updateAllCursorPositionsAfterTextRemoved(position: Long, length: Long) {
        writerStates.filter {
            if (it.cursorPosition == null) false
            else it.cursorPosition >= position
        }.forEach { writerState ->
            writerState.changeCursorPosition(writerState.cursorPosition!! - length)
        }
    }

    fun removeText(writerId: UUID, position: Long, length: Long): Session {
        val updatedDocumentState = documentState.removeText(position, length)
        val textRemoved = TextRemoved(
            sessionId = id,
            writerId = writerId,
            sequenceNumber = nextSessionEventSequenceNumber(),
            broadcasted = false,
            position = position,
            length = length
        )

        generatedSessionEvents.add(textRemoved)

        updateAllCursorPositionsAfterTextRemoved(position, length)

        return copy(
            documentState = updatedDocumentState
        ).copyWithEventSequenceNumberIncremented()
    }

    fun selectText(writerId: UUID, position: Long, length: Long): Session {
        val writerState: WriterState = writerStates.first { it.writerId == writerId }
        ensureSelectedTextConsistency(SelectedText(position, length))
        if (!writerStates.remove(writerState)) throw IllegalStateException()
        val updatedWriterState = writerState.selectText(position, length)
        if (!writerStates.add(updatedWriterState)) throw IllegalStateException()
        return copyWithEventSequenceNumberIncremented()
    }

    fun deselectText(writerId: UUID): Session {
        val writerState: WriterState = writerStates.first { it.writerId == writerId }
        if (!writerStates.remove(writerState)) throw IllegalStateException()
        val updatedWriterState = writerState.deselectText()
        if (!writerStates.add(updatedWriterState)) throw IllegalStateException()
        return copyWithEventSequenceNumberIncremented()
    }

    private fun copyWithEventSequenceNumberIncremented(): Session =
        copy(lastSessionEventSequenceNumber = lastSessionEventSequenceNumber + 1)

    private fun ensureSelectedTextConsistency(
        selectedText: SelectedText
    ) {
        require(selectedText.position >= 0) { "Selected text position must be non-negative, got: ${selectedText.position}" }
        require(selectedText.length > 0) { "Selected text length must be non-negative, got: ${selectedText.length}" }
        val documentLength = documentState.length()
        require(selectedText.position < documentLength) { "Selected text position ${selectedText.position} is beyond document length $documentLength" }
        require(selectedText.position + selectedText.length <= documentLength) {
            "Selected text range [${selectedText.position}, ${selectedText.position + selectedText.length}) extends beyond document length $documentLength"
        }
    }

    private fun ensureCursorPositionConsistency(
        cursorPosition: Long
    ) {
        require(cursorPosition >= 0) { "Cursor position must be non-negative, got: $cursorPosition" }
        val contentLength = documentState.length()
        require(cursorPosition <= contentLength) { "Cursor position $cursorPosition is beyond document length $contentLength" }
    }

    fun findWriterStateByUserId(userId: UUID): WriterState {
        return writerStates.first { it.userId == userId }
    }

    fun findWriterStateByWriterId(writerId: UUID): WriterState {
        return writerStates.first { it.writerId == writerId }
    }

    fun writerExistsByUserId(userId: UUID): Boolean {
        return writerStates.any { it.userId == userId }
    }

    override fun popAllGeneratedSessionEvents(): List<SessionEvent> {
        return generatedSessionEvents.toList().also { generatedSessionEvents.clear() }
    }

    fun addSubscription(writerId: UUID, subscription: Subscription): Session {
        writerStates.find { it.writerId == writerId }?.subscriptions?.add(subscription)
        return this
    }

    fun removeSubscription(writerId: UUID, subscriptionId: String): Session {
        writerStates.find { it.writerId == writerId }?.subscriptions?.removeIf { subscription -> subscription.id == subscriptionId }
        return this
    }

    fun start(): Session {
        if(status != Status.NOT_STARTED) throw IllegalStateException()
        return copy(status = Status.STARTED)
    }

    fun hasWriters(): Boolean {
        return !writerStates.isEmpty()
    }

    fun end(): Session {
        if(status != Status.STARTED) throw IllegalStateException()
        return copy(status = Status.ENDED)
    }
}