package tcla.contexts.tcla.core.application.teammember.search

import tcla.libraries.search.ManyValuesFilter

data class SearchTeamMembersQuery(val filter: ManyValuesFilter<String, String>? = null)
