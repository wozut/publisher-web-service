package tcla.contexts.tcla.webapi.springweb.teammember.create.jsonapi



data class TeamMemberPostResourceAttributes(
    val name: String,
    val email: String,
    val teamId: String
)
