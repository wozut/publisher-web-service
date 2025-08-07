package tcla.contexts.tcla.core.application.organization.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.organization.OrganizationFilterKey
import tcla.contexts.tcla.core.domain.organization.OrganizationRepository
import tcla.contexts.tcla.core.domain.organization.model.OwnerId
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class SearchOrganizationsQueryHandler(
    private val organizationRepository: OrganizationRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule
) {
    fun execute(query: SearchOrganizationsQuery): Either<NonEmptyList<Failure>, SearchOrganizationsSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    query.filter.ensureFilterIsSupported(requesterId)
                        .flatMap { filter -> ensureRequesterOnlyAccessToOwnedResources(requesterId, filter) }
                }
                .flatMap { filter -> organizationRepository.search(filter) }
                .flatMap { organizations -> SearchOrganizationsSuccess(organizations).right() }
        }

    private fun ensureRequesterOnlyAccessToOwnedResources(
        requesterId: String,
        filter: OneValueFilter<OrganizationFilterKey, OwnerId>?
    ): Either<NonEmptyList<Failure>, OneValueFilter<OrganizationFilterKey, OwnerId>?> =
        when (filter) {
            null -> Failure.RequesterDoesNotOwnResource.Organization.nel().left()
            else -> when (filter.key) {
                OrganizationFilterKey.OWNER -> when (filter.operator) {
                    Operator.BinaryOperator.Equal -> when (filter.value.string) {
                        requesterId -> filter.right()
                        else -> Failure.RequesterDoesNotOwnResource.Organization.nel().left()
                    }

                    Operator.NaryOperator.In -> Failure.RequesterDoesNotOwnResource.Organization.nel().left()
                }
            }
        }

    private fun ManyValuesFilter<String, String>?.ensureFilterIsSupported(requesterId: String): Either<NonEmptyList<Failure>, OneValueFilter<OrganizationFilterKey, OwnerId>?> =
        when (this) {
            null -> null.right()
            else -> when (values.isEmpty()) {
                true -> Failure.NoValuesPresentInFilter.nel().left()
                false -> when (key) {
                    "owner" -> when (values.size > 1) {
                        true -> Failure.FilterValuesNotAllowed.nel().left()
                        false -> OneValueFilter(OrganizationFilterKey.OWNER, values.buildFilterValue(requesterId)).right()
                    }

                    else -> Failure.FilterKeyNotAllowed.nel().left()
                }
            }
        }

    private fun List<String>.buildFilterValue(requesterId: String): OwnerId =
        when (val filterValue = first()) {
            "me" -> requesterId
            else -> filterValue
        }.let { OwnerId(it) }
}
