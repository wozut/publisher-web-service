package tcla.contexts.tcla.core.application.team.search

import tcla.libraries.search.ManyValuesFilter

data class SearchTeamsQuery(val filters: List<ManyValuesFilter<String, String>> = listOf())
