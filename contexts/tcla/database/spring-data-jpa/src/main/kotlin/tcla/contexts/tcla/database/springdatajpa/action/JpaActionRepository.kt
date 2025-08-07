package tcla.contexts.tcla.database.springdatajpa.action

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaActionRepository: JpaRepository<JpaAction, UUID> {
    fun findAllByAssessment_Id(uuid: UUID): List<JpaAction>
}
