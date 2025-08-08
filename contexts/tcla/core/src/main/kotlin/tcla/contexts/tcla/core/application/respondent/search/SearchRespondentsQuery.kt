package tcla.contexts.tcla.core.application.respondent.search

import tcla.libraries.search.ManyValuesFilter

data class SearchRespondentsQuery(val filter: ManyValuesFilter<String, String>? = null)
