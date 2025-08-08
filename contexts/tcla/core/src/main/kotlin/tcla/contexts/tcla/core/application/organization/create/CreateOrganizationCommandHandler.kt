package tcla.contexts.tcla.core.application.organization.create

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.CreateOrganizationFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.organization.OrganizationFilterKey
import tcla.contexts.tcla.core.domain.organization.OrganizationRepository
import tcla.contexts.tcla.core.domain.organization.model.*
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.util.UUID

@Named
class CreateOrganizationCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val organizationRepository: OrganizationRepository,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule
) {
    fun execute(command: CreateOrganizationCommand): Either<NonEmptyList<Failure>, CreateOrganizationSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    ensureCurrentPlanAllowsCreateOneMore(requesterId)
                        .flatMap { ensureOrganizationIdNotExistsYet(OrganizationId(UUID.randomUUID())) }
                        .flatMap { id ->
                            Organization(
                                id = id,
                                name = Name(command.name),
                                industry = Industry(command.industry),
                                size = Size(command.size),
                                ownerId = OwnerId(requesterId),
                                maximumAmountOfTeams = MaximumAmountOfTeams(1)
                            ).right()
                        }.flatMap { team -> organizationRepository.save(team) }
                        .flatMap { CreateOrganizationSuccess(it).right() }
                }
        }

    private fun ensureOrganizationIdNotExistsYet(organizationId: OrganizationId): Either<NonEmptyList<Failure>, OrganizationId> =
        organizationRepository.exists(organizationId)
            .flatMap { exists ->
                when (exists) {
                    true -> Failure.EntityAlreadyExists.Organization.nel().left()
                    false -> organizationId.right()
                }
            }

    private fun ensureCurrentPlanAllowsCreateOneMore(requesterId: String): Either<NonEmptyList<Failure>, Unit> =
        organizationRepository.search(OneValueFilter(OrganizationFilterKey.OWNER, OwnerId(requesterId)))
            .flatMap { currentOwnedOrganizations: List<Organization> ->
                when (currentOwnedOrganizations.size) {
                    0 -> Unit.right()
                    else -> CreateOrganizationFailure.CurrentPlanNotAllows.nel().left()
                }
            }
}
