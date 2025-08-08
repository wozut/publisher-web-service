package tcla.contexts.tcla.webapi.springweb.organization.jsonapi

import tcla.contexts.tcla.core.domain.organization.model.Organization


data class OrganizationResource(
    val id: String,
    val attributes: OrganizationResourceAttributes,
) {
    val type: String = ORGANIZATION_JSON_API_TYPE
}

fun Organization.toResource(): OrganizationResource =
    OrganizationResource(
        id = id.uuid.toString(),
        attributes = OrganizationResourceAttributes(
            name = name.string,
            industry = industry.string,
            size = size.string,
            ownerId = ownerId.string,
            maximumAmountOfTeams = maximumAmountOfTeams.int
        )
    )
