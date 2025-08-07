package tcla.contexts.tcla.webapi.springweb.team.jsonapi

import tcla.contexts.tcla.core.domain.team.model.Team


data class TeamResource(
    val id: String,
    val attributes: TeamResourceAttributes,
) {
    val type: String = TEAM_JSON_API_TYPE
}

fun Team.toResource(): TeamResource =
    TeamResource(
        id = id.uuid.toString(),
        attributes = TeamResourceAttributes(
            name = name.string,
            timeZone = timeZone.id,
            ownerId = ownerId.string,
            isArchived = isArchived
        )
    )
