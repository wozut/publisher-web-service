package tcla.contexts.tcla.database.springdatajpa.organization

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaOrganizationRepository: JpaRepository<JpaOrganization, UUID> {
    fun findAllByOwnerId(ownerId: String): List<JpaOrganization>
}
