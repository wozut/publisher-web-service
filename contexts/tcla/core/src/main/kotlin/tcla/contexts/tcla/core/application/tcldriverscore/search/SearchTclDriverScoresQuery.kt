package tcla.contexts.tcla.core.application.tcldriverscore.search

import tcla.libraries.search.ManyValuesFilter

data class SearchTclDriverScoresQuery(val filter: ManyValuesFilter<String, String>? = null)
