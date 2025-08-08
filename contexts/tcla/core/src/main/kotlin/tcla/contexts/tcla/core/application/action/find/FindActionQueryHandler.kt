package tcla.contexts.tcla.core.application.action.find

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.action.ActionRepository
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.core.domain.action.rule.RequesterOwnsAction
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid

@Named
class FindActionQueryHandler(
    private val actionRepository: ActionRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val requesterOwnsAction: RequesterOwnsAction
) {
    fun execute(query: FindActionQuery): Either<NonEmptyList<Failure>, FindActionSuccess> =
        transactionExecutor.transactional(IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { _: String ->
                    query.id.toUuid()
                        .mapLeft { Failure.StringIsNotUuid.ActionId.nel() }
                }.flatMap { uuid -> actionRepository.find(Action.ActionId(uuid)) }
                .flatMap { action -> requesterOwnsAction.ensure(action).flatMap { action.right() } }
                .flatMap { action -> FindActionSuccess(action).right() }
        }
}
