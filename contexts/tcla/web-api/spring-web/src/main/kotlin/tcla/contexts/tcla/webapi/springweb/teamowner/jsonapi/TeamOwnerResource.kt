package tcla.contexts.tcla.webapi.springweb.teamowner.jsonapi

import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner


data class TeamOwnerResource(
    val id: String,
    val attributes: TeamOwnerResourceAttributes,
) {
    val type: String = TEAM_OWNER_JSON_API_TYPE
}

fun TeamOwner.toResource(): TeamOwnerResource =
    TeamOwnerResource(
        id = id.string,
        attributes = TeamOwnerResourceAttributes(
            name = name.string
        )
    )
