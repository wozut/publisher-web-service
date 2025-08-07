package tcla.contexts.tcla.core.application.organization.search

import tcla.libraries.search.ManyValuesFilter

data class SearchOrganizationsQuery(val filter: ManyValuesFilter<String, String>? = null)
