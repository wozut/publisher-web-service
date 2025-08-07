package tcla.contexts.tcla.core.application.teammember.create

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.Email
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.team.TeamFilterKey
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teammember.CheckTeamMemberExistenceFailure
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.contexts.tcla.core.domain.teammember.model.Name
import tcla.contexts.tcla.core.domain.teammember.model.TeamMember
import tcla.contexts.tcla.core.domain.teammember.model.TeamMemberId
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class CreateTeamMemberCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule
) {
    fun execute(command: CreateTeamMemberCommand): Either<CreateTeamMemberFailure, CreateTeamMemberSuccess> {
        return transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            ensureRequestIsAuthenticated()
                .flatMap { requesterId ->
                    val id = TeamMemberId(UUID.randomUUID())
                    teamMemberRepository.exists(id)
                        .mapLeft { checkTeamMemberExistenceFailure ->
                            when (checkTeamMemberExistenceFailure) {
                                is CheckTeamMemberExistenceFailure.DatabaseException -> TODO()
                            }
                        }.flatMap { exists ->
                            when (exists) {
                                true -> TODO()
                                false -> id.right()
                            }
                        }.flatMap { teamMemberId ->
                            ensureRequesterOwnsTeam(requesterId, command.teamId)
                                .flatMap {
                                    ensureNameIsValid(command.name)
                                        .flatMap { name ->
                                            ensureEmailIsValid(command.email)
                                                .flatMap { email ->
                                                    ensureTeamIdIsValid(command.teamId)
                                                        .flatMap { teamId ->
                                                            TeamMember(
                                                                id = teamMemberId,
                                                                name = name,
                                                                email = email,
                                                                teamId = teamId
                                                            ).right()
                                                        }

                                                }
                                        }
                                }
                        }.flatMap { teamMember ->
                            teamMemberRepository.save(teamMember)
                                .mapLeft { TODO() }
                        }.flatMap { CreateTeamMemberSuccess(it).right() }
                }
        }
    }

    private fun ensureRequesterOwnsTeam(
        requesterId: String,
        teamUuidAsString: String
    ): Either<CreateTeamMemberFailure, Unit> =
        teamUuidAsString.toUuid()
            .mapLeft { TODO() }
            .flatMap { teamUuid ->
                TeamId(teamUuid).right()
            }.flatMap { teamId ->
                teamRepository.search(OneValueFilter(TeamFilterKey.OWNER, TeamOwnerId(requesterId)))
                    .mapLeft { TODO() }
                    .flatMap { teams: List<Team> ->
                        when (teams.any { team -> team.id == teamId }) {
                            true -> Unit.right()
                            false -> TODO()
                        }
                    }
            }

    private fun ensureNameIsValid(name: String): Either<CreateTeamMemberFailure, Name> {
        return Name(name).mapLeft { CreateTeamMemberFailure.InvalidName }
    }

    private fun ensureEmailIsValid(email: String): Either<CreateTeamMemberFailure, Email> {
        return Email(email).mapLeft { CreateTeamMemberFailure.InvalidEmail }
    }

    private fun ensureTeamIdIsValid(teamId: String): Either<CreateTeamMemberFailure, TeamId> {
        return teamId.toUuid().mapLeft { TODO() }.flatMap { TeamId(it).right() }
    }

    private fun ensureRequestIsAuthenticated(): Either<CreateTeamMemberFailure, String> =
        requestIsAuthenticatedRule.ensure()
            .mapLeft { CreateTeamMemberFailure.RequestNotAuthenticated }
}
