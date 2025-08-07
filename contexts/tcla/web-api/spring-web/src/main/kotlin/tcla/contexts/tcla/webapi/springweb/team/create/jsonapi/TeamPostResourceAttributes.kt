package tcla.contexts.tcla.webapi.springweb.team.create.jsonapi



data class TeamPostResourceAttributes(
    val name: String,
    val timeZone: String,
    val organizationId: String
)
