package tcla.contexts.tcla.database.springdatajpa.questionnaire

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.mapOrAccumulate
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import tcla.contexts.tcla.core.domain.questionnaire.model.Responses
import tcla.contexts.tcla.database.springdatajpa.AuditMetadata
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessment
import tcla.contexts.tcla.database.springdatajpa.question.JpaQuestion
import tcla.contexts.tcla.database.springdatajpa.questionnairefilling.JpaQuestionnaireFilling
import java.sql.Timestamp
import java.util.UUID

@Entity(name = "questionnaire")
@Table(name = "survey", schema = "tcla")
@EntityListeners(AuditingEntityListener::class)
data class JpaQuestionnaire(
    @Id val id: UUID,
    @Column(nullable = false)
    val startDate: Timestamp,
    @Column(name = "\"end_date\"", nullable = false)
    val endDate: Timestamp,
    @Column(nullable = true)
    val externalQuestionnaireId: String?,
    @Column(nullable = false)
    val externalQuestionnaireIsPublic: Boolean,
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id", foreignKey = ForeignKey(name = "fk_tcla_survey"))
    val questions: MutableList<JpaQuestion>,
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "assessment_id", foreignKey = ForeignKey(name = "fk_tcla_assessment"))
    val assessment: JpaAssessment,
    @Column(nullable = false)
    val maximumAmountToBeCollected: Int,
    @Column(nullable = false)
    val minimumRateRequired: Double,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionnaire")
    val questionnaireFillings: MutableList<JpaQuestionnaireFilling>
) {
    @Embedded
    var auditMetadata: AuditMetadata = AuditMetadata()

    fun toDomain(): Either<NonEmptyList<Failure>, Questionnaire> =
        questions.mapOrAccumulate { jpaQuestion: JpaQuestion ->
            jpaQuestion.toDomain().bind()
        }.flatMap {
            ResponseAcceptanceInterval(startDate.toInstant(), endDate.toInstant())
                .mapLeft { nonEmptyListOf(it) }
                .flatMap { interval ->
                    Questionnaire(
                        id = QuestionnaireId(id),
                        responseAcceptanceInterval = interval,
                        externalQuestionnaireIsPublic = externalQuestionnaireIsPublic,
                        externalQuestionnaireId = when (externalQuestionnaireId) {
                            null -> null
                            else -> ExternalQuestionnaireId(externalQuestionnaireId)
                        },
                        questions = it.toSet(),
                        assessmentId = AssessmentId(assessment.id),
                        responses = Responses(
                            maximumAmountToBeCollected = Responses.MaximumAmountToBeCollected(maximumAmountToBeCollected),
                            minimumRateRequired = Responses.MinimumRateRequired(minimumRateRequired),
                            collection = questionnaireFillings.map(JpaQuestionnaireFilling::toDomain).toSet()
                        )
                    ).right()
                }
        }
}

fun Questionnaire.toJpa(jpaAssessment: JpaAssessment): JpaQuestionnaire =
    JpaQuestionnaire(
        id = id.uuid,
        startDate = Timestamp(responseAcceptanceInterval.start.toEpochMilli()),
        endDate = Timestamp(responseAcceptanceInterval.end.toEpochMilli()),
        externalQuestionnaireId = when (val externalQuestionnaireId = externalQuestionnaireId) {
            null -> null
            else -> externalQuestionnaireId.value
        },
        externalQuestionnaireIsPublic = externalQuestionnaireIsPublic,
        questions = mutableListOf(),
        assessment = jpaAssessment,
        maximumAmountToBeCollected = responses.maximumAmountToBeCollected.int,
        minimumRateRequired = responses.minimumRateRequired.double,
        questionnaireFillings = mutableListOf()
    )
