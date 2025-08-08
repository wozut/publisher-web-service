package tcla.contexts.tcla.core.application.team.find

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.team.rule.RequesterOwnsTeamRule
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid

@Named
class FindTeamQueryHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requesterOwnsTeamRule: RequesterOwnsTeamRule
) {
    fun execute(query: FindTeamQuery): Either<NonEmptyList<Failure>, FindTeamSuccess> =
        transactionExecutor.transactional(IsolationLevel.READ_COMMITTED) {
             query.id.toUuid()
                .mapLeft { _:StringNotConformsUuid -> nonEmptyListOf(Failure.StringIsNotUuid.TeamId) }
                 .flatMap { uuid -> TeamId(uuid).right() }
                 .flatMap { teamId -> requesterOwnsTeamRule.ensure(teamId) }
                .flatMap { team -> FindTeamSuccess(team).right() }
        }
}
