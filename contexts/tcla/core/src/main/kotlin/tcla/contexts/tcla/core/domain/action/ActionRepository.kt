package tcla.contexts.tcla.core.domain.action

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.libraries.search.Filter

interface ActionRepository {
    fun exists(id: Action.ActionId): Either<NonEmptyList<Failure>, Boolean>
    fun save(action: Action): Either<NonEmptyList<Failure>, Action>
    fun find(id: Action.ActionId): Either<NonEmptyList<Failure>, Action>
    fun search(filter: Filter<ActionFilterKey>? = null): Either<NonEmptyList<Failure>, List<Action>>
}
