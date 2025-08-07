package tcla.contexts.tcla.core.application.teamowner.search

import tcla.libraries.search.ManyValuesFilter

data class SearchTeamOwnersQuery(val filters: List<ManyValuesFilter<String, String>> = listOf())
