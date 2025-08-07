package tcla.contexts.tcla.core.domain.question.model

import tcla.contexts.tcla.core.domain.answeroption.AnswerOptionMother
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption
import java.util.UUID

object MultipleChoiceQuestionMother {
    fun default(
        id: QuestionId = QuestionId(UUID.randomUUID()),
        order: Order = Order(1),
        answerOptions: Set<AnswerOption> = setOf(AnswerOptionMother.default()),
        label: Question.Label = Question.Label.Gender
    ): MultipleChoiceQuestion = MultipleChoiceQuestion(
        id = id,
        order = order,
        answerOptions = answerOptions,
        label = label
    )

    fun build(
        id: UUID = UUID.randomUUID(),
        order: Int = 1,
        answerOptions: Set<AnswerOption> = setOf(AnswerOptionMother.default()),
        label: Question.Label = Question.Label.Gender
    ): MultipleChoiceQuestion = MultipleChoiceQuestion(
        id = QuestionId(id),
        order = Order(order),
        answerOptions = answerOptions,
        label = label
    )
}
