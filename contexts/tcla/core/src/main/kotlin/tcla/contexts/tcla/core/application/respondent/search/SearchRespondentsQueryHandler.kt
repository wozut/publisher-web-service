package tcla.contexts.tcla.core.application.respondent.search

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
import tcla.contexts.tcla.core.domain.respondent.RespondentFilterKey
import tcla.contexts.tcla.core.domain.respondent.RespondentRepository
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class SearchRespondentsQueryHandler(
    private val respondentRepository: RespondentRepository,
    private val transactionExecutor: TransactionExecutor,
    private val requesterOwnsAssessmentRule: RequesterOwnsAssessmentRule
) {
    fun execute(query: SearchRespondentsQuery): Either<NonEmptyList<Failure>, SearchRespondentsSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            query.filter.ensureFilterIsSupported()
                .flatMap { filter -> ensureRequesterOnlyAccessToOwnedResources(filter) }
                .flatMap { filter -> respondentRepository.search(filter) }
                .flatMap { respondents -> SearchRespondentsSuccess(respondents).right() }
        }

    private fun ManyValuesFilter<String, String>?.ensureFilterIsSupported(): Either<NonEmptyList<Failure>, OneValueFilter<RespondentFilterKey, AssessmentId>?> =
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
                                .flatMap { assessmentUuid: UUID -> OneValueFilter(RespondentFilterKey.ASSESSMENT, AssessmentId(assessmentUuid)).right() }
                        }
                    }

                    else -> Failure.FilterKeyNotAllowed.nel().left()
                }
            }
        }

    private fun ensureRequesterOnlyAccessToOwnedResources(
        filter: OneValueFilter<RespondentFilterKey, AssessmentId>?
    ): Either<NonEmptyList<Failure>, OneValueFilter<RespondentFilterKey, AssessmentId>?> =
        when (filter) {
            null -> nonEmptyListOf(Failure.RequestNotAuthenticated).left()
            else -> when (filter.key) {
                RespondentFilterKey.ASSESSMENT -> when (filter.operator) {
                    Operator.BinaryOperator.Equal -> {
                        requesterOwnsAssessmentRule.ensure(filter.value)
                            .flatMap { filter.right() }
                    }

                    Operator.NaryOperator.In -> TODO()
                }
            }
        }
}
