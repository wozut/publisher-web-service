package tcla.contexts.tcla.core.domain.organization.model

data class Organization(
    val id: OrganizationId,
    val name: Name,
    val industry: Industry,
    val size: Size,
    val ownerId: OwnerId,
    val maximumAmountOfTeams: MaximumAmountOfTeams
)
