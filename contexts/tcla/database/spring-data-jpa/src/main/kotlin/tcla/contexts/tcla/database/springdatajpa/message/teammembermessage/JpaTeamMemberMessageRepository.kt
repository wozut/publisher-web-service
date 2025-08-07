package tcla.contexts.tcla.database.springdatajpa.message.teammembermessage

import org.springframework.data.jpa.repository.JpaRepository
import java.sql.Timestamp
import java.util.UUID

interface JpaTeamMemberMessageRepository: JpaRepository<JpaTeamMemberMessage, UUID> {
    fun findAllByActuallySentAtIsNullAndScheduledToBeSentAtBefore(timestamp: Timestamp): List<JpaTeamMemberMessage>
    fun findAllByQuestionnaire_IdAndActuallySentAtIsNull(questionnaireId: UUID): List<JpaTeamMemberMessage>
}
