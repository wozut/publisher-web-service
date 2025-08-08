package tcla.contexts.tcla.database.springdatajpa.message.accountmessage

import org.springframework.data.jpa.repository.JpaRepository
import java.sql.Timestamp
import java.util.UUID

interface JpaAccountMessageRepository: JpaRepository<JpaAccountMessage, UUID> {
    fun findAllByActuallySentAtIsNullAndScheduledToBeSentAtBefore(timestamp: Timestamp): List<JpaAccountMessage>
    fun findAllByQuestionnaire_IdAndActuallySentAtIsNull(questionnaireId: UUID): List<JpaAccountMessage>
}
