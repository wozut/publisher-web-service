package tcla.contexts.realtimecollaboration.webapi.websocket.collaborativesession

import tcla.contexts.realtimecollaboration.webapi.websocket.CollaboratorState
import tcla.contexts.realtimecollaboration.webapi.websocket.DocumentState
import tcla.contexts.realtimecollaboration.webapi.websocket.SelectedText
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CollaborativeEvent
import tcla.contexts.realtimecollaboration.webapi.websocket.events.CursorPositionChanged
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextAdded
import tcla.contexts.realtimecollaboration.webapi.websocket.events.TextRemoved
import java.awt.SystemColor.text
import java.util.*
import kotlin.collections.toList

data class CollaborativeSession(
    val id: UUID,
    val documentState: DocumentState,
    val collaboratorStates: MutableSet<CollaboratorState>,
    val lastCollaborativeEventSequenceNumber: Long,
    val generatedCollaborativeEvents: MutableList<CollaborativeEvent> = mutableListOf()
) : CollaborativeEventGenerator {

    private fun nextCollaborativeEventSequenceNumber(): Long = lastCollaborativeEventSequenceNumber + 1

    fun addCollaboratorState(collaboratorState: CollaboratorState): CollaborativeSession {
        println("Adding collaboratorState: $collaboratorState")
        if (collaboratorStates.any { it.userId == collaboratorState.userId }) throw IllegalArgumentException()
        if (collaboratorState.cursorPosition != null) ensureCursorPositionConsistency(collaboratorState.cursorPosition)
        if (collaboratorState.selectedText != null) ensureSelectedTextConsistency(collaboratorState.selectedText)
        if (!collaboratorStates.add(collaboratorState)) throw IllegalArgumentException()
        return copyWithEventSequenceNumberIncremented()
    }

    fun removeCollaboratorState(userId: UUID): CollaborativeSession {
        val collaboratorState: CollaboratorState = collaboratorStates.firstOrNull { it.userId == userId }
            ?: throw IllegalArgumentException("Collaborator not found. UserId: $userId")
        if (!collaboratorStates.remove(collaboratorState)) throw IllegalStateException()
        return copyWithEventSequenceNumberIncremented()
    }

    fun changeCursorPosition(collaboratorId: UUID, newPosition: Long): CollaborativeSession {
        val collaboratorState: CollaboratorState = collaboratorStates.first { it.collaboratorId == collaboratorId }
        ensureCursorPositionConsistency(newPosition)
        if (!collaboratorStates.remove(collaboratorState)) throw IllegalStateException()
        val updatedCollaboratorState = collaboratorState.changeCursorPosition(newPosition)
        if (!collaboratorStates.add(updatedCollaboratorState)) throw IllegalStateException()

        val cursorPositionChanged = CursorPositionChanged(
            collaborativeSessionId = id,
            collaboratorId = collaboratorId,
            sequenceNumber = nextCollaborativeEventSequenceNumber(),
            broadcasted = false,
            newPosition = newPosition
        )

        generatedCollaborativeEvents.add(cursorPositionChanged)

        return copyWithEventSequenceNumberIncremented()
    }

    fun addText(collaboratorId: UUID, position: Long, text: String): CollaborativeSession {
        val updatedDocumentState = documentState.addText(position, text)
        val textAdded = TextAdded(
            collaborativeSessionId = id,
            collaboratorId = collaboratorId,
            sequenceNumber = nextCollaborativeEventSequenceNumber(),
            broadcasted = false,
            position = position,
            text = text
        )

        generatedCollaborativeEvents.add(textAdded)

        updateAllCursorPositionsAfterTextAdded(position, text.length.toLong())

        return copy(
            documentState = updatedDocumentState
        ).copyWithEventSequenceNumberIncremented()
    }

    private fun updateAllCursorPositionsAfterTextAdded(position: Long, length: Long) {
        collaboratorStates.filter {
            if (it.cursorPosition == null) false
            else it.cursorPosition >= position
        }.forEach { collaboratorState ->
            collaboratorState.changeCursorPosition(collaboratorState.cursorPosition!! + length)
        }
    }

    private fun updateAllCursorPositionsAfterTextRemoved(position: Long, length: Long) {
        collaboratorStates.filter {
            if (it.cursorPosition == null) false
            else it.cursorPosition >= position
        }.forEach { collaboratorState ->
            collaboratorState.changeCursorPosition(collaboratorState.cursorPosition!! - length)
        }
    }

    fun removeText(collaboratorId: UUID, position: Long, length: Long): CollaborativeSession {
        val updatedDocumentState = documentState.removeText(position, length)
        val textRemoved = TextRemoved(
            collaborativeSessionId = id,
            collaboratorId = collaboratorId,
            sequenceNumber = nextCollaborativeEventSequenceNumber(),
            broadcasted = false,
            position = position,
            length = length
        )

        generatedCollaborativeEvents.add(textRemoved)

        updateAllCursorPositionsAfterTextRemoved(position, length)

        return copy(
            documentState = updatedDocumentState
        ).copyWithEventSequenceNumberIncremented()
    }

    fun selectText(collaboratorId: UUID, position: Long, length: Long): CollaborativeSession {
        val collaboratorState: CollaboratorState = collaboratorStates.first { it.collaboratorId == collaboratorId }
        ensureSelectedTextConsistency(SelectedText(position, length))
        if (!collaboratorStates.remove(collaboratorState)) throw IllegalStateException()
        val updatedCollaboratorState = collaboratorState.selectText(position, length)
        if (!collaboratorStates.add(updatedCollaboratorState)) throw IllegalStateException()
        return copyWithEventSequenceNumberIncremented()
    }

    fun deselectText(collaboratorId: UUID): CollaborativeSession {
        val collaboratorState: CollaboratorState = collaboratorStates.first { it.collaboratorId == collaboratorId }
        if (!collaboratorStates.remove(collaboratorState)) throw IllegalStateException()
        val updatedCollaboratorState = collaboratorState.deselectText()
        if (!collaboratorStates.add(updatedCollaboratorState)) throw IllegalStateException()
        return copyWithEventSequenceNumberIncremented()
    }

    private fun copyWithEventSequenceNumberIncremented(): CollaborativeSession =
        copy(lastCollaborativeEventSequenceNumber = lastCollaborativeEventSequenceNumber + 1)

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

    fun findCollaboratorStateByUserId(userId: UUID): CollaboratorState {
        return collaboratorStates.first { it.userId == userId }
    }

    fun findCollaboratorStateByCollaboratorId(collaboratorId: UUID): CollaboratorState {
        return collaboratorStates.first { it.collaboratorId == collaboratorId }
    }

    fun collaboratorExistsByUserId(userId: UUID): Boolean {
        return collaboratorStates.any { it.userId == userId }
    }

    override fun popAllGeneratedCollaborativeEvents(): List<CollaborativeEvent> {
        return generatedCollaborativeEvents.toList().also { generatedCollaborativeEvents.clear() }
    }
}