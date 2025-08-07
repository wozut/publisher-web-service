package tcla.contexts.tcla.core.application.response.search

import tcla.libraries.search.ManyValuesFilter

data class SearchResponsesQuery(val filters: List<ManyValuesFilter<String, String>> = listOf())
