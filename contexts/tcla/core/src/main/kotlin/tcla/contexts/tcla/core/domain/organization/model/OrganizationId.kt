package tcla.contexts.tcla.core.domain.organization.model

import java.util.UUID

@JvmInline
value class OrganizationId(val uuid: UUID) {
    override fun toString(): String = uuid.toString()
}
