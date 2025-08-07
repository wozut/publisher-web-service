package tcla.contexts.tcla.core.application.tcldriver.search

import tcla.libraries.search.ManyValuesFilter

data class SearchTclDriversQuery(val filter: ManyValuesFilter<String, String>? = null)
