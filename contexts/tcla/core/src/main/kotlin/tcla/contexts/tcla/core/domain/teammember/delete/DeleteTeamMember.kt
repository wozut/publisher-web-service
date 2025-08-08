package tcla.contexts.tcla.core.domain.teammember.delete

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.contexts.tcla.core.domain.teammember.model.TeamMember
import tcla.contexts.tcla.core.domain.teammember.model.TeamMemberId
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class DeleteTeamMember(
    private val teamMemberRepository: TeamMemberRepository,
    private val teamRepository: TeamRepository,
    private val transactionExecutor: TransactionExecutor,
) {
    fun execute(teamMemberId: TeamMemberId, requesterId: String): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            findTeamMember(teamMemberId)
                .flatMap { teamMember -> findTeam(teamMember) }
                .flatMap { team -> ensureRequesterOwnsTeam(requesterId, team.ownerId) }
                .flatMap { teamMemberRepository.delete(teamMemberId) }
        }

    private fun ensureRequesterOwnsTeam(
        requesterId: String,
        ownerId: TeamOwnerId
    ): Either<NonEmptyList<Failure>, Unit> =
        either {
            ensure(requesterId == ownerId.string) { nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Team) }
        }

    private fun findTeam(teamMember: TeamMember): Either<NonEmptyList<Failure>, Team> =
        teamRepository.find(teamMember.teamId)

    private fun findTeamMember(teamMemberId: TeamMemberId): Either<NonEmptyList<Failure>, TeamMember> =
        teamMemberRepository.find(teamMemberId)
}
