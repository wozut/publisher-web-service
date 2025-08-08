package tcla.contexts.tcla.core.application.teamowner.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.SearchTeamOwnersFailure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.teamowner.TeamOwnerFilterKey
import tcla.contexts.tcla.core.domain.teamowner.TeamOwnerRepository
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class SearchTeamOwnersQueryHandler(
    private val transactionExecutor: TransactionExecutor,
    private val teamOwnerRepository: TeamOwnerRepository
) {
    fun execute(query: SearchTeamOwnersQuery): Either<NonEmptyList<Failure>, SearchTeamOwnersSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            query.filters.ensureFiltersAreSupported()
                .flatMap { filter ->
                    teamOwnerRepository.search(filter).mapLeft { failure -> nonEmptyListOf(failure) }
                }.flatMap { teamOwners -> SearchTeamOwnersSuccess(teamOwners).right() }
        }

    private fun List<ManyValuesFilter<String, String>>.ensureFiltersAreSupported(): Either<NonEmptyList<SearchTeamOwnersFailure>, OneValueFilter<TeamOwnerFilterKey, out Any>> =
        when (this.isEmpty()) {
            true -> nonEmptyListOf(SearchTeamOwnersFailure.AtLeastOneFilterRequired).left()
            else -> this.mapOrAccumulate { filter ->
                val values = filter.values
                when (values.isEmpty()) {
                    true -> SearchTeamOwnersFailure.NoValuesPresentInFilter.left()
                    false -> when (val key = filter.key) {
                        "assessment" -> when (values.size > 1) {
                            true -> SearchTeamOwnersFailure.NotAllowedFilterValues(filter).left()
                            false -> values.first().toUuid()
                                .mapLeft { _: StringNotConformsUuid -> SearchTeamOwnersFailure.AssessmentIdNotConformsUUID }
                                .flatMap { assessmentUuid: UUID ->
                                    OneValueFilter(TeamOwnerFilterKey.ASSESSMENT, AssessmentId(assessmentUuid)).right()
                                }
                        }

                        else -> SearchTeamOwnersFailure.NotAllowedFilterKey(key).left()
                    }
                }.bind()
            }.flatMap { filters: List<OneValueFilter<TeamOwnerFilterKey, out Any>> ->
                when (filters.size) {
                    0 -> nonEmptyListOf(SearchTeamOwnersFailure.AtLeastOneFilterRequired).left()
                    1 -> filters.first().right()
                    else -> nonEmptyListOf(SearchTeamOwnersFailure.TooManyFilters).left()
                }
            }
        }
}
