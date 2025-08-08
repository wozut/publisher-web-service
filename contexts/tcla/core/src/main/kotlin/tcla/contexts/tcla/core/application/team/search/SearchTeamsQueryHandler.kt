package tcla.contexts.tcla.core.application.team.search

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
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.organization.rule.RequesterOwnsOrganizationRule
import tcla.contexts.tcla.core.domain.team.TeamFilterKey
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid

@Named
class SearchTeamsQueryHandler(
    private val teamRepository: TeamRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val requesterOwnsOrganizationRule: RequesterOwnsOrganizationRule
) {
    fun execute(query: SearchTeamsQuery): Either<NonEmptyList<Failure>, SearchTeamsSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    query.filters.ensureFiltersAreSupported(requesterId)
                        .flatMap { filter -> ensureRequesterOnlyAccessToOwnedResources(requesterId, filter) }
                }
                .flatMap { filter -> teamRepository.search(filter) }
                .flatMap { teams -> SearchTeamsSuccess(teams).right() }
        }

    private fun ensureRequesterOnlyAccessToOwnedResources(
        requesterId: String,
        filter: OneValueFilter<TeamFilterKey, out Any>?
    ): Either<NonEmptyList<Failure>, OneValueFilter<TeamFilterKey, out Any>?> =
        when (filter) {
            null -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Team).left()
            else -> when (filter.key) {
                TeamFilterKey.OWNER -> when (val value = filter.value) {
                    is TeamOwnerId -> when (filter.operator) {
                        Operator.BinaryOperator.Equal -> when (value.string) {
                            requesterId -> filter.right()
                            else -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Team).left()
                        }

                        Operator.NaryOperator.In -> TODO()
                    }

                    else -> TODO()
                }


                TeamFilterKey.ORGANIZATION -> when (val value = filter.value) {
                    is OrganizationId -> when (filter.operator) {
                        Operator.BinaryOperator.Equal ->
                            requesterOwnsOrganizationRule.ensure(value)
                                .flatMap { filter.right() }

                        else -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Team).left()
                    }

                    Operator.NaryOperator.In -> TODO()

                    else -> TODO()
                }

            }
        }

    private fun List<ManyValuesFilter<String, String>>.ensureFiltersAreSupported(requesterId: String): Either<NonEmptyList<Failure>, OneValueFilter<TeamFilterKey, out Any>?> =
        when (this.isEmpty()) {
            true -> null.right()
            else -> this.map { filter ->
                val values = filter.values
                when (values.isEmpty()) {
                    true -> Failure.NoValuesPresentInFilter.left()
                    false -> when (filter.key) {
                        "owner" -> when (values.size > 1) {
                            true -> Failure.FilterValuesNotAllowed.left()
                            false -> OneValueFilter(
                                TeamFilterKey.OWNER,
                                values.buildOwnerFilterValue(requesterId)
                            ).right()
                        }

                        "organization" -> when (values.size > 1) {
                            true -> Failure.FilterValuesNotAllowed.left()
                            false -> values.buildOrganizationFilterValue()
                                .flatMap { OneValueFilter(TeamFilterKey.ORGANIZATION, it).right() }
                        }

                        else -> Failure.FilterKeyNotAllowed.left()
                    }
                }
            }.flattenOrAccumulate()
                .flatMap { filters: List<OneValueFilter<TeamFilterKey, out Any>> ->
                    when (filters.size) {
                        0 -> null.right()
                        1 -> filters.first().right()
                        else -> nonEmptyListOf(Failure.TooManyFilters).left()
                    }
                }
        }

    private fun List<String>.buildOwnerFilterValue(requesterId: String): TeamOwnerId =
        when (val filterValue = first()) {
            "me" -> requesterId
            else -> filterValue
        }.let { TeamOwnerId(it) }

    private fun List<String>.buildOrganizationFilterValue(): Either<Failure, OrganizationId> =
        first().toUuid().mapLeft { Failure.StringIsNotUuid.OrganizationId }
            .flatMap { uuid -> OrganizationId(uuid).right() }
}
