package tcla.contexts.tcla.database.springdatajpa.question

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.OneToMany
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestion
import tcla.contexts.tcla.core.domain.question.model.Order
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.question.model.QuestionId
import tcla.contexts.tcla.database.springdatajpa.answeroption.JpaAnswerOption
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaire
import java.util.UUID

@Entity(name = "multiple_choice_question")
@Table(name = "multiple_choice_question", schema = "tcla")
@PrimaryKeyJoinColumn(foreignKey = ForeignKey(name = "fk_tcla_question"))
class JpaMultipleChoiceQuestion(
    id: UUID,
    label: String,
    order: Int,
    questionnaire: JpaQuestionnaire,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "multipleChoiceQuestion")
    val answerOptions: MutableList<JpaAnswerOption>
) : JpaQuestion(
    id = id,
    label = label,
    order = order,
    questionnaire = questionnaire
) {
    override fun toDomain(): Either<Failure, MultipleChoiceQuestion> =
        label
            .toDomainLabel()
            .flatMap {
            multipleChoiceQuestion(it).right()
        }

    private fun multipleChoiceQuestion(label: Question.Label) =
        MultipleChoiceQuestion(
            id = QuestionId(id),
            order = Order(order),
            answerOptions = answerOptions.map(JpaAnswerOption::toDomain).toSet(),
            label = label
        )

}

fun MultipleChoiceQuestion.toJpa(jpaQuestionnaire: JpaQuestionnaire): JpaMultipleChoiceQuestion =
    JpaMultipleChoiceQuestion(
        id = id.uuid,
        label = label.value,
        order = order.value,
        answerOptions = mutableListOf(),
        questionnaire = jpaQuestionnaire
    )
