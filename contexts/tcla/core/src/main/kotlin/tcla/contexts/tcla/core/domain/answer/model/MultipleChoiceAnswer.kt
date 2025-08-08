package tcla.contexts.tcla.core.domain.answer.model

import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption

class MultipleChoiceAnswer(
    id: AnswerId,
    val answerOption: AnswerOption
) : Answer(id)
