package tcla.contexts.tcla.database.springdatajpa.tcldriverscore

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaTclDriverScoreRepository: JpaRepository<JpaTclDriverScore, UUID> {
    fun findAllByAssessment_Id(assessmentId: UUID): List<JpaTclDriverScore>
    fun findAllByAssessment_ResultsShareableToken(token: String): List<JpaTclDriverScore>

    fun deleteAllByAssessment_Id(assessmentId: UUID)
}
