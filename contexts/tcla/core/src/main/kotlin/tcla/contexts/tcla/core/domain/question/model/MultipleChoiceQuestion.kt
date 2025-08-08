package tcla.contexts.tcla.core.domain.question.model

import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption


class MultipleChoiceQuestion(
    id: QuestionId,
    label: Label,
    order: Order,
    val answerOptions: Set<AnswerOption>
) : Question(
    id = id,
    order = order,
    label = label
)
