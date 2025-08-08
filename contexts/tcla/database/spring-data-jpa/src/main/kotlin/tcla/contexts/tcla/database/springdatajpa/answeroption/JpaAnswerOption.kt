package tcla.contexts.tcla.database.springdatajpa.answeroption

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOptionId
import tcla.contexts.tcla.core.domain.answeroptionforanalysis.model.AnswerOptionForAnalysis
import tcla.contexts.tcla.core.domain.answeroptionforanalysis.model.AnswerOptionForAnalysisId
import tcla.contexts.tcla.core.domain.answeroptionforanalysis.model.ValueForAnalysis
import tcla.contexts.tcla.database.springdatajpa.question.JpaMultipleChoiceQuestion
import java.util.UUID

@Entity(name = "answer_option")
@Table(name = "answer_option", schema = "tcla")
data class JpaAnswerOption(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val value: String,
    @Column(name = "\"order\"", nullable = false)
    val order: Int,
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "multiple_choice_question_id",
        foreignKey = ForeignKey(name = "fk_tcla_multiple_choice_question")
    )
    val multipleChoiceQuestion: JpaMultipleChoiceQuestion
) {
    fun toDomainForAnalysis(): Either<Failure, AnswerOptionForAnalysis> =
        multipleChoiceQuestion.toDomain()
            .flatMap {
                AnswerOptionForAnalysis(
                    id = AnswerOptionForAnalysisId(id),
                    value = ValueForAnalysis(value),
                    multipleChoiceQuestion = it
                ).right()
            }

    fun toDomain(): AnswerOption =
        AnswerOption(
            id = AnswerOptionId(id),
            value = AnswerOption.Value(value),
            order = AnswerOption.Order(order)
        )
}

fun AnswerOption.toJpa(jpaMultipleChoiceQuestion: JpaMultipleChoiceQuestion): JpaAnswerOption {
    return JpaAnswerOption(
        id = id.value,
        value = value.value,
        order = order.value,
        multipleChoiceQuestion = jpaMultipleChoiceQuestion
    )
}
