package tcla.contexts.tcla.webapi.springweb.organization.jsonapi


data class OrganizationResourceAttributes(
    val name: String,
    val industry: String,
    val size: String,
    val ownerId: String,
    val maximumAmountOfTeams: Int
)
