package tcla.contexts.tcla.database.springdatajpa.questionnairefilling

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaQuestionnaireFillingRepository: JpaRepository<JpaQuestionnaireFilling, UUID> {
    fun findAllByQuestionnaire_Id(questionnaireId: UUID): List<JpaQuestionnaireFilling>
}
