package tcla.contexts.tcla.webapi.springweb.teammember.jsonapi

import tcla.contexts.tcla.core.domain.teammember.model.TeamMember


data class TeamMemberResource(
    val id: String,
    val attributes: TeamMemberResourceAttributes,
) {
    val type: String = TEAM_MEMBER_JSON_API_TYPE
}

fun TeamMember.toResource(): TeamMemberResource =
    TeamMemberResource(
        id = id.uuid.toString(),
        attributes = TeamMemberResourceAttributes(
            name = name.string,
            email = email.string
        )
    )
