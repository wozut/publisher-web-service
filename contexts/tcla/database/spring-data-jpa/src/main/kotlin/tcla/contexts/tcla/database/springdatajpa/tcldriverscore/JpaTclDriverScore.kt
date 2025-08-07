package tcla.contexts.tcla.database.springdatajpa.tcldriverscore

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreId
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreValue
import tcla.contexts.tcla.database.springdatajpa.AuditMetadata
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessment
import java.util.UUID

@Entity(name = "tcl_driver_score")
@Table(name = "tcl_driver_score", schema = "tcla")
@EntityListeners(AuditingEntityListener::class)
data class JpaTclDriverScore(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val value: Double,
    @Column(nullable = false)
    val tclDriverId: UUID,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "assessment_id", foreignKey = ForeignKey(name = "fk_tcla_assessment"))
    val assessment: JpaAssessment
) {
    @Embedded
    var auditMetadata: AuditMetadata = AuditMetadata()

    fun toDomain(): TclDriverScore =
        TclDriverScore(
            id = TclDriverScoreId(id),
            tclDriverId = TclDriverId(tclDriverId),
            assessmentId = AssessmentId(assessment.id),
            value = TclDriverScoreValue(value)
        )
}


