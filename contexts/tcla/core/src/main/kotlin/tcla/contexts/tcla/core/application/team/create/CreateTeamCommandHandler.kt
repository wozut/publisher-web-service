package tcla.contexts.tcla.core.application.team.create

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.CreateTeamFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.checkwhetherorganizationcanhaveonemoreteam.CheckWhetherOrganizationCanHaveOneMoreTeam
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.organization.rule.RequesterOwnsOrganizationRule
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class CreateTeamCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val teamRepository: TeamRepository,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val checkWhetherOrganizationCanHaveOneMoreTeam: CheckWhetherOrganizationCanHaveOneMoreTeam,
    private val requesterOwnsOrganizationRule: RequesterOwnsOrganizationRule
) {
    fun execute(command: CreateTeamCommand): Either<NonEmptyList<Failure>, CreateTeamSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    ensureRequesterOwnsOrganization(command.organizationId)
                        .flatMap { organizationId ->
                            ensureOrganizationCanHaveOneMoreTeam(organizationId)
                                .flatMap { ensureNotExistsYet(TeamId(UUID.randomUUID())) }
                                .flatMap { teamId ->
                                    Team(
                                        id = teamId,
                                        name = command.name,
                                        timeZone = command.timeZone,
                                        ownerId = TeamOwnerId(requesterId),
                                        organizationId = organizationId,
                                        isArchived = false
                                    )
                                }
                        }
                }
                .flatMap { team -> teamRepository.save(team).mapLeft { nonEmptyListOf(it) } }
                .flatMap { CreateTeamSuccess(it).right() }
        }

    private fun ensureRequesterOwnsOrganization(organizationUuidAsString: String): Either<NonEmptyList<Failure>, OrganizationId> =
        organizationUuidAsString.toUuid()
            .mapLeft { Failure.StringIsNotUuid.OrganizationId.nel() }
            .flatMap { organizationUuid -> OrganizationId(organizationUuid).right() }
            .flatMap { organizationId -> requesterOwnsOrganizationRule.ensure(organizationId) }

    private fun ensureNotExistsYet(id: TeamId): Either<NonEmptyList<Failure>, TeamId> =
        teamRepository.exists(id)
            .mapLeft { nonEmptyListOf(it) }
            .flatMap { exists ->
                when (exists) {
                    true -> TODO()
                    false -> id.right()
                }
            }

    private fun ensureOrganizationCanHaveOneMoreTeam(organizationId: OrganizationId): Either<NonEmptyList<Failure>, Unit> =
        checkWhetherOrganizationCanHaveOneMoreTeam.execute(organizationId)
            .flatMap { teamCanBeCreated ->
                when (teamCanBeCreated) {
                    true -> Unit.right()
                    false -> CreateTeamFailure.MaximumAmountOfTeamsPerOrganizationReached.nel().left()
                }
            }
}
