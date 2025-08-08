package tcla.contexts.tcla.core.application.organization.checkwhetherorganizationcanhaveonemoreteam

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.checkwhetherorganizationcanhaveonemoreteam.CheckWhetherOrganizationCanHaveOneMoreTeam
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid

@Named
class CheckWhetherOrganizationCanHaveOneMoreTeamQueryHandler(
    private val checkWhetherOrganizationCanHaveOneMoreTeam: CheckWhetherOrganizationCanHaveOneMoreTeam,
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule
) {
    fun execute(query: CheckWhetherOrganizationCanHaveOneMoreTeamQuery): Either<NonEmptyList<Failure>, CheckWhetherOrganizationCanHaveOneMoreTeamSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { query.organizationId.toUuid().mapLeft { Failure.StringIsNotUuid.OrganizationId.nel() } }
                .flatMap { organizationUuid -> checkWhetherOrganizationCanHaveOneMoreTeam.execute(OrganizationId(organizationUuid)) }
                .flatMap { result -> CheckWhetherOrganizationCanHaveOneMoreTeamSuccess(result).right() }
        }
}
