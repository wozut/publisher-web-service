package tcla.contexts.tcla.database.springdatajpa.respondent

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaRespondentRepository: JpaRepository<JpaRespondent, UUID> {
    fun findAllByAssessment_Id(assessmentId: UUID): List<JpaRespondent>
}
