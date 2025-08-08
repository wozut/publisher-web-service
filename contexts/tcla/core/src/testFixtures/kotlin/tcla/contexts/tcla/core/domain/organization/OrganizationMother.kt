package tcla.contexts.tcla.core.domain.organization

import tcla.contexts.tcla.core.domain.organization.model.*
import java.util.UUID

object OrganizationMother {
    fun id(value: UUID = UUID.randomUUID()): OrganizationId = OrganizationId(value)

    fun default(
        id: OrganizationId = OrganizationId(UUID.randomUUID()),
        name: Name = Name("ACME"),
        industry: Industry = Industry("Tech"),
        size: Size = Size("small"),
        ownerId: OwnerId = OwnerId("123456"),
        maximumAmountOfTeams: MaximumAmountOfTeams = MaximumAmountOfTeams(1)
    ): Organization = Organization(
        id = id,
        name = name,
        industry = industry,
        size = size,
        ownerId = ownerId,
        maximumAmountOfTeams = maximumAmountOfTeams
    )
}
