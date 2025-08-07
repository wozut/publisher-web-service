package tcla.contexts.tcla.core.application.assessment.search

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
import tcla.contexts.tcla.core.domain.assessment.AssessmentFilterKey
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.team.rule.RequesterOwnsTeamRule
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class SearchAssessmentsQueryHandler(
    private val assessmentRepository: AssessmentRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val sortAssessments: SortAssessments,
    private val requesterOwnsTeamRule: RequesterOwnsTeamRule
) {
    fun execute(query: SearchAssessmentsQuery): Either<NonEmptyList<Failure>, SearchAssessmentsSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    query.filters.ensureFiltersAreSupported(requesterId)
                        .flatMap { filter -> ensureRequesterOnlyAccessToOwnedResources(requesterId, filter) }
                }
                .flatMap { filter -> assessmentRepository.search(filter) }
                .flatMap { assessments -> sortAssessments.execute(assessments) }
                .flatMap { assessments -> SearchAssessmentsSuccess(assessments).right() }
        }

    private fun ensureRequesterOnlyAccessToOwnedResources(
        requesterId: String,
        filter: OneValueFilter<AssessmentFilterKey, out Any>?
    ): Either<NonEmptyList<Failure>, OneValueFilter<AssessmentFilterKey, out Any>?> =
        when (filter) {
            null -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Assessment).left()
            else -> when (filter.key) {
                AssessmentFilterKey.OWNER -> when (val value = filter.value) {
                    is String -> when (filter.operator) {
                        Operator.BinaryOperator.Equal -> when (value) {
                            requesterId -> filter.right()
                            else -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Assessment).left()
                        }

                        Operator.NaryOperator.In -> TODO()
                    }

                    else -> TODO()
                }


                AssessmentFilterKey.TEAM -> when (val value = filter.value) {
                    is TeamId -> when (filter.operator) {
                        Operator.BinaryOperator.Equal ->
                            requesterOwnsTeamRule.ensure(value)
                                .flatMap { filter.right() }

                        else -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Assessment).left()
                    }

                    Operator.NaryOperator.In -> TODO()

                    else -> TODO()
                }

                AssessmentFilterKey.CURRENT_STEP -> TODO()
            }
        }

    private fun List<ManyValuesFilter<String, String>>.ensureFiltersAreSupported(requesterId: String): Either<NonEmptyList<Failure>, OneValueFilter<AssessmentFilterKey, out Any>?> =
        when (this.isEmpty()) {
            true -> null.right()
            else -> this.map { filter ->
                val values = filter.values
                when (values.isEmpty()) {
                    true -> Failure.NoValuesPresentInFilter.left()
                    false -> when (filter.key) {
                        "team" -> when (values.size > 1) {
                            true -> Failure.FilterValuesNotAllowed.left()
                            false -> values.first().toUuid()
                                .mapLeft { _: StringNotConformsUuid -> Failure.StringIsNotUuid.TeamId }
                                .flatMap { teamUuid: UUID ->
                                    OneValueFilter(AssessmentFilterKey.TEAM, TeamId(teamUuid)).right()
                                }
                        }

                        "owner" -> when (values.size > 1) {
                            true -> Failure.FilterValuesNotAllowed.left()
                            false -> OneValueFilter(
                                AssessmentFilterKey.OWNER,
                                values.buildFilterValue(requesterId)
                            ).right()
                        }

                        else -> Failure.FilterKeyNotAllowed.left()
                    }
                }
            }.flattenOrAccumulate()
                .flatMap { filters: List<OneValueFilter<AssessmentFilterKey, out Any>> ->
                    when (filters.size) {
                        0 -> null.right()
                        1 -> filters.first().right()
                        else -> nonEmptyListOf(Failure.TooManyFilters).left()
                    }
                }
        }

    private fun List<String>.buildFilterValue(requesterId: String): String =
        when (val value = first()) {
            "me" -> requesterId
            else -> value
        }

}
