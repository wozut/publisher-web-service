package tcla.contexts.tcla.core.domain.team.model

import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import java.util.UUID

object TeamMother {
    fun id(value: UUID = UUID.randomUUID()): TeamId = TeamId(value)

    fun default(
        id: TeamId = TeamId(UUID.randomUUID()),
        name: String = "Avengers",
        timeZone: String = "Europe/Brussels",
        ownerId: TeamOwnerId = TeamOwnerId("auth|g6231876td6713231e"),
        organizationId: OrganizationId = OrganizationId(UUID.randomUUID()),
        isArchived: Boolean = false
    ): Team = Team.invoke(
        id = id,
        name = name,
        timeZone = timeZone,
        ownerId = ownerId,
        organizationId = organizationId,
        isArchived = isArchived
    ).fold({ TODO() }, { it })
}
