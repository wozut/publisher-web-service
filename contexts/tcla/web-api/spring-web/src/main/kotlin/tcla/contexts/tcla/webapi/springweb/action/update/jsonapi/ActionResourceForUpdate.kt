package tcla.contexts.tcla.webapi.springweb.action.update.jsonapi

data class ActionResourceForUpdate(
    val type: String,
    val id: String,
    val attributes: Map<String, Any?>?
)
