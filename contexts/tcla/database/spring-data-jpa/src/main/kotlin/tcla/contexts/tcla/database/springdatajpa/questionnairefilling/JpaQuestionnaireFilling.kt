package tcla.contexts.tcla.database.springdatajpa.questionnairefilling

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.mapOrAccumulate
import arrow.core.right
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questionnairefilling.model.ExternalId
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFillingId
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysis
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysisId
import tcla.contexts.tcla.database.springdatajpa.answer.JpaAnswer
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaire
import java.util.UUID

@Entity(name = "questionnaire_filling")
@Table(name = "response", schema = "tcla")
data class JpaQuestionnaireFilling(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val externalId: String,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id", foreignKey = ForeignKey(name = "fk_tcla_survey"))
    val questionnaire: JpaQuestionnaire,
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_filling_id", foreignKey = ForeignKey(name = "fk_tcla_response"))
    val answers: MutableList<JpaAnswer>
) {
    fun toDomainForAnalysis(): Either<NonEmptyList<Failure>,QuestionnaireFillingForAnalysis> =
        answers.mapOrAccumulate { jpaAnswer: JpaAnswer -> jpaAnswer.toDomainForAnalysis().bind() }
            .flatMap {
                QuestionnaireFillingForAnalysis(
                    id = QuestionnaireFillingForAnalysisId(id),
                    answers = it
                ).right()
            }

    fun toDomain(): QuestionnaireFilling = QuestionnaireFilling(
        id = QuestionnaireFillingId(id),
        externalId = ExternalId(externalId),
        answers = answers.map(JpaAnswer::toDomain).toSet()
    )
}

fun QuestionnaireFilling.toJpa(jpaQuestionnaire: JpaQuestionnaire): JpaQuestionnaireFilling =
    JpaQuestionnaireFilling(
        id = this.id.uuid,
        externalId = externalId.string,
        questionnaire = jpaQuestionnaire,
        answers = mutableListOf()
    )
