package tcla.contexts.tcla.core.application.teammember.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teammember.TeamMemberFilterKey
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class SearchTeamMembersQueryHandler(
    private val teamMemberRepository: TeamMemberRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val teamRepository: TeamRepository
) {
    fun execute(query: SearchTeamMembersQuery): Either<NonEmptyList<Failure>, SearchTeamMembersSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    query.filter.ensureFilterIsSupported()
                        .flatMap { filter -> ensureRequesterOnlyAccessToOwnedResources(requesterId, filter) }
                        .flatMap { filter -> teamMemberRepository.search(filter).mapLeft { it.nel() } }
                }
                .flatMap { teamMembers -> SearchTeamMembersSuccess(teamMembers).right() }
        }

    private fun ManyValuesFilter<String, String>?.ensureFilterIsSupported(): Either<NonEmptyList<Failure>, OneValueFilter<TeamMemberFilterKey, TeamId>?> =
        when (this) {
            null -> null.right()
            else -> when (values.isEmpty()) {
                true -> Failure.NoValuesPresentInFilter.nel().left()
                false -> when (key) {
                    "team" -> when (values.size > 1) {
                        true -> Failure.FilterValuesNotAllowed.nel().left()
                        false -> {
                            values.first().toUuid()
                                .mapLeft { _: StringNotConformsUuid -> Failure.StringIsNotUuid.TeamId.nel() }
                                .flatMap { teamUuid: UUID ->
                                    OneValueFilter(
                                        TeamMemberFilterKey.TEAM,
                                        TeamId(teamUuid)
                                    ).right()
                                }
                        }
                    }

                    else -> Failure.FilterKeyNotAllowed.nel().left()
                }
            }
        }

    private fun ensureRequesterOnlyAccessToOwnedResources(
        requesterId: String,
        filter: OneValueFilter<TeamMemberFilterKey, TeamId>?
    ): Either<NonEmptyList<Failure>, OneValueFilter<TeamMemberFilterKey, TeamId>?> = when (filter) {
        null -> Failure.RequesterDoesNotOwnResource.TeamMember.nel().left()
        else -> when (val key = filter.key) {
            TeamMemberFilterKey.TEAM -> when (val value: TeamId = filter.value) {
                else -> teamRepository.find(filter.value)
                    .flatMap { team ->
                        when (team.ownerId.string) {
                            requesterId -> filter.right()
                            else -> Failure.RequesterDoesNotOwnResource.TeamMember.nel().left()
                        }
                    }
            }
        }
    }

}
