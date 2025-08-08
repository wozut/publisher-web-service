package tcla.contexts.tcla.webapi.springweb.team.update.jsonapi

data class TeamResourceForUpdate(
    val type: String,
    val id: String,
    val attributes: Map<String, Any?>?
)
