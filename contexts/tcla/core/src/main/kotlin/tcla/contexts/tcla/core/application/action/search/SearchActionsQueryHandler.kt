package tcla.contexts.tcla.core.application.action.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.action.ActionFilterKey
import tcla.contexts.tcla.core.domain.action.ActionRepository
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid

@Named
class SearchActionsQueryHandler(
    private val actionRepository: ActionRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val assessmentRepository: AssessmentRepository,
    private val teamRepository: TeamRepository
) {
    fun execute(query: SearchActionsQuery): Either<NonEmptyList<Failure>, SearchActionsSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    query.filter.ensureFilterIsSupported()
                        .flatMap { filter -> ensureRequesterOnlyAccessToOwnedResources(requesterId, filter) }
                }
                .flatMap { filter -> actionRepository.search(filter) }
                .flatMap { actions -> SearchActionsSuccess(actions).right() }
        }

    private fun ensureRequesterOnlyAccessToOwnedResources(
        requesterId: String,
        filter: OneValueFilter<ActionFilterKey, AssessmentId>?
    ): Either<NonEmptyList<Failure>, OneValueFilter<ActionFilterKey, AssessmentId>?> =
        when (filter) {
            null -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Action).left()
            else -> when (filter.key) {
                ActionFilterKey.ASSESSMENT -> when (filter.operator) {
                    Operator.BinaryOperator.Equal -> {
                        assessmentRepository.find(filter.value)
                            .flatMap { teamRepository.find(it.teamId) }
                            .flatMap { team: Team ->
                                when(team.ownerId.string) {
                                    requesterId -> filter.right()
                                    else -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Action).left()
                                }
                            }
                    }

                    Operator.NaryOperator.In -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Action).left()
                }
            }
        }

    private fun ManyValuesFilter<String, String>?.ensureFilterIsSupported(): Either<NonEmptyList<Failure>, OneValueFilter<ActionFilterKey, AssessmentId>?> =
        when (this) {
            null -> null.right()
            else -> when (values.isEmpty()) {
                true -> Failure.NoValuesPresentInFilter.nel().left()
                false -> when (key) {
                    "assessment" -> when (values.size > 1) {
                        true -> nonEmptyListOf(Failure.FilterValuesNotAllowed).left()
                        false -> values
                            .first()
                            .toUuid()
                            .mapLeft { Failure.StringIsNotUuid.AssessmentId.nel() }
                            .flatMap { AssessmentId(it).right() }
                            .flatMap { assessmentId -> OneValueFilter(ActionFilterKey.ASSESSMENT, assessmentId).right() }
                    }

                    else -> nonEmptyListOf(Failure.FilterKeyNotAllowed).left()
                }
            }
        }
}
