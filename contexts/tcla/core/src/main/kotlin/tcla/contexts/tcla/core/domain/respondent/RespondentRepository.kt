package tcla.contexts.tcla.core.domain.respondent

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.respondent.model.Respondent
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.libraries.search.Filter

interface RespondentRepository {
    fun exists(id: RespondentId): Either<NonEmptyList<Failure>, Boolean>
    fun save(respondent: Respondent): Either<NonEmptyList<Failure>, Respondent>
    fun search(filter: Filter<RespondentFilterKey>? = null): Either<NonEmptyList<Failure>, List<Respondent>>
}
