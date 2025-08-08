package tcla.contexts.tcla.core.domain.checkwhetherorganizationcanhaveonemoreteam

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.organization.OrganizationRepository
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.team.TeamFilterKey
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class CheckWhetherOrganizationCanHaveOneMoreTeam(
    private val transactionExecutor: TransactionExecutor,
    private val organizationRepository: OrganizationRepository,
    private val teamRepository: TeamRepository
) {
    fun execute(organizationId: OrganizationId): Either<NonEmptyList<Failure>, Boolean> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            teamRepository.search(OneValueFilter(TeamFilterKey.ORGANIZATION, organizationId))
                .flatMap { teams ->
                    val notArchivedTeams = teams.filter { team -> !team.isArchived }
                    organizationRepository.find(organizationId)
                        .flatMap { organization ->
                            (notArchivedTeams.size < organization.maximumAmountOfTeams.int).right()
                        }
                }
        }
}
