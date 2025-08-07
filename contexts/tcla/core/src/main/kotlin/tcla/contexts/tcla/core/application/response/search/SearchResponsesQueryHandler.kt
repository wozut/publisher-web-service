package tcla.contexts.tcla.core.application.response.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefilling.QuestionnaireFillingRepository
import tcla.contexts.tcla.core.domain.questionnairefilling.ResponseFilterKey
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class SearchResponsesQueryHandler(
    private val transactionExecutor: TransactionExecutor,
    private val questionnaireFillingRepository: QuestionnaireFillingRepository,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule
) {
    fun execute(query: SearchResponsesQuery): Either<NonEmptyList<SearchResponsesFailure>, SearchResponsesSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            ensureRequestIsAuthenticated()
                .flatMap { query.filters.ensureFiltersAreSupported() }
                .flatMap { filter ->
                    questionnaireFillingRepository.search(filter).mapLeft { failure -> nonEmptyListOf(failure) }
                }.flatMap { responses -> SearchResponsesSuccess(responses).right() }
        }

    private fun ensureRequestIsAuthenticated(): Either<NonEmptyList<SearchResponsesFailure>, String> =
        requestIsAuthenticatedRule.ensure()
            .mapLeft { nonEmptyListOf(SearchResponsesFailure.RequestNotAuthenticated) }

    private fun List<ManyValuesFilter<String, String>>.ensureFiltersAreSupported(): Either<NonEmptyList<SearchResponsesFailure>, OneValueFilter<ResponseFilterKey, out Any>> =
        when (this.isEmpty()) {
            true -> nonEmptyListOf(SearchResponsesFailure.AtLeastOneFilterRequired).left()
            else -> this.mapOrAccumulate { filter ->
                val values = filter.values
                when (values.isEmpty()) {
                    true -> SearchResponsesFailure.NoValuesPresentInFilter.left()
                    false -> when (val key = filter.key) {
                        "survey" -> when (values.size > 1) {
                            true -> SearchResponsesFailure.NotAllowedFilterValues(filter).left()
                            false -> values.first().toUuid()
                                .mapLeft { _: StringNotConformsUuid -> SearchResponsesFailure.SurveyIdNotConformsUUID }
                                .flatMap { surveyUuid: UUID ->
                                    OneValueFilter(ResponseFilterKey.QUESTIONNAIRE, QuestionnaireId(surveyUuid)).right()
                                }
                        }

                        else -> SearchResponsesFailure.NotAllowedFilterKey(key).left()
                    }
                }.bind()
            }.flatMap { filters: List<OneValueFilter<ResponseFilterKey, out Any>> ->
                when (filters.size) {
                    0 -> nonEmptyListOf(SearchResponsesFailure.AtLeastOneFilterRequired).left()
                    1 -> filters.first().right()
                    else -> nonEmptyListOf(SearchResponsesFailure.TooManyFilters).left()
                }
            }
        }
}
