package tcla.contexts.tcla.core.application.response.search

import tcla.libraries.search.ManyValuesFilter

sealed class SearchResponsesFailure {

    data object RequestNotAuthenticated : SearchResponsesFailure()
    data object AssessmentNotFound : SearchResponsesFailure()
    data class DatabaseException(val exception: Throwable) : SearchResponsesFailure()
    data object UnsupportedDatabaseFilter : SearchResponsesFailure()

    data object AtLeastOneFilterRequired : SearchResponsesFailure()
    data object NoValuesPresentInFilter : SearchResponsesFailure() {
        const val PUBLIC_DESCRIPTION: String = "No values present in filter"
    }

    data class NotAllowedFilterKey(val filterKey: String): SearchResponsesFailure() {
        companion object {
            const val PUBLIC_DESCRIPTION: String = "Not allowed filter key"
        }
    }

    data object TooManyFilters: SearchResponsesFailure() {
        const val PUBLIC_DESCRIPTION: String = "Too many filters"
    }

    data class NotAllowedFilterValues(val filter: ManyValuesFilter<String, String>): SearchResponsesFailure() {
        companion object {
            const val PUBLIC_DESCRIPTION: String = "Not allowed filter values"
        }
    }

    data object SurveyIdNotConformsUUID : SearchResponsesFailure()
}
