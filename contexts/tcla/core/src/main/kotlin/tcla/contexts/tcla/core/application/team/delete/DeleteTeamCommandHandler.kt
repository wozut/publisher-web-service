package tcla.contexts.tcla.core.application.team.delete

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flattenOrAccumulate
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teammember.TeamMemberFilterKey
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.contexts.tcla.core.domain.teammember.delete.DeleteTeamMember
import tcla.contexts.tcla.core.domain.teammember.model.TeamMember
import tcla.contexts.tcla.core.domain.teammember.model.TeamMemberId
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class DeleteTeamCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val deleteTeamMember: DeleteTeamMember,
    private val teamMemberRepository: TeamMemberRepository,
    private val teamRepository: TeamRepository
) {
    fun execute(command: DeleteTeamCommand): Either<NonEmptyList<Failure>, DeleteTeamSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            ensureRequestIsAuthenticated().flatMap { requesterId ->
                command.id.toTeamUuid()
                    .flatMap { teamUuid ->
                        ensureRequesterOwnsTeam(teamUuid, requesterId)
                            .flatMap { searchTeamMembers(teamUuid) }
                            .flatMap { teamMembers -> deleteTeamMembers(teamMembers, requesterId) }
                            .flatMap { deleteTeam(teamUuid) }
                    }
            }
        }

    private fun ensureRequesterOwnsTeam(teamUuid: UUID, requesterId: String): Either<NonEmptyList<Failure>, Unit> =
        teamRepository.find(TeamId(teamUuid))
            .flatMap { team ->
                when (team.ownerId.string) {
                    requesterId -> Unit.right()
                    else -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Team).left()
                }
            }

    private fun String.toTeamUuid(): Either<NonEmptyList<Failure>, UUID> =
        toUuid()
            .mapLeft { _: StringNotConformsUuid -> nonEmptyListOf(Failure.StringIsNotUuid.TeamId) }

    private fun deleteTeam(uuid: UUID): Either<NonEmptyList<Failure>, DeleteTeamSuccess> =
        teamRepository.delete(TeamId(uuid))
            .mapLeft { nonEmptyListOf(it) }
            .flatMap { DeleteTeamSuccess.right() }

    private fun deleteTeamMembers(
        teamMembers: List<TeamMember>,
        requesterId: String
    ): Either<NonEmptyList<Failure>, Unit> =
        teamMembers.map { teamMember -> deleteTeamMember(teamMember.id.uuid, requesterId) }
            .flattenOrAccumulate()
            .flatMap { Unit.right() }

    private fun searchTeamMembers(uuid: UUID): Either<NonEmptyList<Failure>, List<TeamMember>> =
        teamMemberRepository.search(OneValueFilter(TeamMemberFilterKey.TEAM, TeamId(uuid)))
            .mapLeft { failure -> nonEmptyListOf(failure) }

    private fun deleteTeamMember(uuid: UUID, requesterId: String): Either<NonEmptyList<Failure>, Unit> =
        deleteTeamMember.execute(TeamMemberId(uuid), requesterId)

    private fun ensureRequestIsAuthenticated(): Either<NonEmptyList<Failure>, String> =
        requestIsAuthenticatedRule.ensure()
            .mapLeft { nonEmptyListOf(Failure.RequestNotAuthenticated) }
}
