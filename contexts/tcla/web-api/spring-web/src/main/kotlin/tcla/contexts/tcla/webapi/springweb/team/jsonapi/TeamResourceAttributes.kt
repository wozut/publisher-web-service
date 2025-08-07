package tcla.contexts.tcla.webapi.springweb.team.jsonapi


data class TeamResourceAttributes(
    val name: String,
    val timeZone: String,
    val ownerId: String,
    val isArchived: Boolean
)
