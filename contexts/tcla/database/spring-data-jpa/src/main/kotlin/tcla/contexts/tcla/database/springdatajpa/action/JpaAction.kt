package tcla.contexts.tcla.database.springdatajpa.action

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
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.database.springdatajpa.AuditMetadata
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessment
import java.util.UUID

@Entity(name = "action")
@Table(name = "action", schema ="tcla")
@EntityListeners(AuditingEntityListener::class)
data class JpaAction(
    @Id
    val id: UUID,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "assessment_id", foreignKey = ForeignKey(name = "fk_tcla_assessment"))
    val assessment: JpaAssessment,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val targetQuestionsToThinkAbout: String,
    @Column(nullable = false)
    val targetTclDrivers: String,
    @Column(nullable = false)
    val description: String,
    @Column(nullable = true)
    val context: String?,
    @Column(nullable = true)
    val challenges: String?,
    @Column(nullable = true)
    val goals: String?,
    @Column(nullable = false)
    val isArchived: Boolean
) {
    @Embedded
    var auditMetadata: AuditMetadata = AuditMetadata()

    fun toDomain(
        targetQuestionsToThinkAbout: Action.TargetQuestionsToThinkAbout,
        targetTclDrivers: Action.TargetTclDrivers
    ): Action = Action(
        id = Action.ActionId(id),
        assessmentId = AssessmentId(assessment.id),
        title = Action.Title(title),
        targetQuestionsToThinkAbout = targetQuestionsToThinkAbout,
        targetTclDrivers = targetTclDrivers,
        description = Action.Description(description),
        context = context?.let { Action.Context(it) },
        challenges = challenges?.let { Action.Challenges(it) },
        goals = goals?.let { Action.Goals(it) },
        isArchived = isArchived
    )
}

fun Action.toJpa(jpaAssessment: JpaAssessment): JpaAction = JpaAction(
    id = id.value,
    assessment = jpaAssessment,
    title = title.value,
    targetQuestionsToThinkAbout = this.targetQuestionsToThinkAbout.toJpa(),
    targetTclDrivers = this.targetTclDrivers.toJpa(),
    description = description.value,
    context = context?.value,
    challenges = challenges?.value,
    goals = goals?.value,
    isArchived = isArchived
)

private fun Action.TargetTclDrivers.toJpa(): String =
    this.value.joinToString(",") { tclDriver -> tclDriver.id.value.toString() }

private fun Action.TargetQuestionsToThinkAbout.toJpa(): String =
    this.value.joinToString(",") { questionToThinkAbout -> questionToThinkAbout.id.value.toString() }
