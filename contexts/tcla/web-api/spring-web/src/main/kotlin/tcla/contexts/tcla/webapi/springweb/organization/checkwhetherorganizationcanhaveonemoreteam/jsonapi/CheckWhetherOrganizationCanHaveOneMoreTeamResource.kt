package tcla.contexts.tcla.webapi.springweb.organization.checkwhetherorganizationcanhaveonemoreteam.jsonapi

import java.util.UUID

data class CheckWhetherOrganizationCanHaveOneMoreTeamResource(
    val id: String,
    val attributes: CheckWhetherOrganizationCanHaveOneMoreTeamResourceAttributes,
) {
    val type: String = CHECK_WHETHER_ORGANIZATION_CAN_HAVE_ONE_MORE_TEAM_JSON_API_TYPE
}

fun Boolean.toResource(): CheckWhetherOrganizationCanHaveOneMoreTeamResource =
    CheckWhetherOrganizationCanHaveOneMoreTeamResource(
        id = UUID.randomUUID().toString(),
        attributes = CheckWhetherOrganizationCanHaveOneMoreTeamResourceAttributes(
            result = this
        )
    )
