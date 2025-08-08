package tcla.contexts.tcla.database.springdatajpa.respondent

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
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
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.InvalidEmail
import tcla.contexts.tcla.core.domain.Email
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.respondent.model.Respondent
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.contexts.tcla.database.springdatajpa.AuditMetadata
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessment
import java.util.UUID

@Entity(name = "respondent")
@Table(name = "respondent", schema = "tcla")
@EntityListeners(AuditingEntityListener::class)
data class JpaRespondent(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val email: String,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "assessment_id", foreignKey = ForeignKey(name = "fk_tcla_assessment"))
    val assessment: JpaAssessment
) {
    @Embedded
    var auditMetadata: AuditMetadata = AuditMetadata()

    fun toDomain(): Either<NonEmptyList<Failure>, Respondent> =
        Email(email)
            .mapLeft { _: InvalidEmail -> Failure.UnableToTransformIntoDomainData.Respondent.nel() }
            .flatMap { domainEmail ->
                Respondent(
                    id = RespondentId(id),
                    name = name,
                    email = domainEmail,
                    assessmentId = AssessmentId(assessment.id)
                ).right()
            }
}

fun Respondent.toJpa(jpaAssessment: JpaAssessment): JpaRespondent =
    JpaRespondent(
        id = id.uuid,
        name = name,
        email = email.string,
        assessment = jpaAssessment
    )
