package tcla.contexts.tcla.core.domain.answer

import arrow.core.Either
import tcla.contexts.tcla.core.application.answer.search.SearchAnswerFailure
import tcla.contexts.tcla.core.domain.answer.model.Answer
import tcla.libraries.search.Filter

interface AnswerRepository {
    fun search(filter: Filter<AnswerFilterKey>? = null): Either<SearchAnswerFailure, List<Answer>>
}
