package tcla.contexts.tcla.core.application.tcldriverscore.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.rule.RequesterOwnsAssessmentRule
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreFilterKey
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreRepository
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class SearchTclDriverScoresQueryHandler(
    private val tclDriverScoreRepository: TclDriverScoreRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requesterOwnsAssessmentRule: RequesterOwnsAssessmentRule
) {
    fun execute(query: SearchTclDriverScoresQuery): Either<NonEmptyList<Failure>, SearchTclDriverScoresSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            query.filter.ensureFilterIsSupported()
                .flatMap { filter -> ensureRequesterOnlyAccessToOwnedResources(filter) }
                .flatMap { filter -> tclDriverScoreRepository.search(filter) }
                .flatMap { tclDriverScores -> SearchTclDriverScoresSuccess(tclDriverScores).right() }
        }

    private fun ManyValuesFilter<String, String>?.ensureFilterIsSupported(): Either<NonEmptyList<Failure>, OneValueFilter<TclDriverScoreFilterKey, AssessmentId>?> =
        when (this) {
            null -> null.right()
            else -> when (values.isEmpty()) {
                true -> Failure.NoValuesPresentInFilter.nel().left()
                false -> when (key) {
                    "assessment" -> when (values.size > 1) {
                        true -> Failure.FilterValuesNotAllowed.nel().left()
                        false -> {
                            values.first().toUuid()
                                .mapLeft { _: StringNotConformsUuid -> Failure.StringIsNotUuid.AssessmentId.nel() }
                                .flatMap { assessmentUuid: UUID -> OneValueFilter(TclDriverScoreFilterKey.ASSESSMENT, AssessmentId(assessmentUuid)).right() }
                        }
                    }

                    else -> Failure.FilterKeyNotAllowed.nel().left()
                }
            }
        }

    private fun ensureRequesterOnlyAccessToOwnedResources(
        filter: OneValueFilter<TclDriverScoreFilterKey, AssessmentId>?
    ): Either<NonEmptyList<Failure>, OneValueFilter<TclDriverScoreFilterKey, AssessmentId>?> =
        when (filter) {
            null -> nonEmptyListOf(Failure.RequestNotAuthenticated).left()
            else -> when (filter.key) {
                TclDriverScoreFilterKey.ASSESSMENT -> when (filter.operator) {
                    Operator.BinaryOperator.Equal -> {
                        requesterOwnsAssessmentRule.ensure(filter.value)
                            .flatMap { filter.right() }
                    }

                    Operator.NaryOperator.In -> TODO()
                }

                TclDriverScoreFilterKey.RESULTS_SHAREABLE_TOKEN -> TODO()
            }
        }
}
