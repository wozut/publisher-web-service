package tcla.contexts.tcla.core.domain.question

import arrow.core.Either
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.question.model.QuestionId

interface QuestionRepository {
    fun noneExists(questionIds: List<QuestionId>): Either<Failure, Boolean>
    fun find(questionId: QuestionId): Either<Failure, Question>
}
