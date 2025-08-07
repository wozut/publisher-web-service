package tcla.contexts.tcla.core.application.action.search

import tcla.libraries.search.ManyValuesFilter

data class SearchActionsQuery(val filter: ManyValuesFilter<String, String>? = null)
