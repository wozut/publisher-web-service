package tcla.contexts.tcla.core.application.report.search

import tcla.libraries.search.ManyValuesFilter

data class SearchReportsQuery(
    val filters: List<ManyValuesFilter<String, String>> = listOf()
)
