package tcla.contexts.tcla.database.springdatajpa.answer

import arrow.core.Either
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.answer.search.SearchAnswerFailure
import tcla.contexts.tcla.core.domain.answer.AnswerFilterKey
import tcla.contexts.tcla.core.domain.answer.AnswerRepository
import tcla.contexts.tcla.core.domain.answer.model.Answer
import tcla.libraries.search.Filter

@Named
class SqlAnswerRepository: AnswerRepository {
    override fun search(filter: Filter<AnswerFilterKey>?): Either<SearchAnswerFailure, List<Answer>> {
        TODO("Not yet implemented")
    }
}
