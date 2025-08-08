package tcla.contexts.tcla.core.application.organization.search

import tcla.contexts.tcla.core.domain.organization.model.Organization

data class SearchOrganizationsSuccess(val organizations: List<Organization>)
