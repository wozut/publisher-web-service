package tcla.contexts.tcla.database.springdatajpa.organization

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import tcla.contexts.tcla.core.domain.organization.model.*
import tcla.contexts.tcla.database.springdatajpa.AuditMetadata
import java.util.UUID

@Entity(name = "organization")
@Table(name = "organization", schema = "tcla")
@EntityListeners(AuditingEntityListener::class)
data class JpaOrganization(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val industry: String,
    @Column(nullable = false)
    val size: String,
    @Column(nullable = false)
    val ownerId: String,
    @Column(nullable = false)
    val maximumAmountOfTeams: Int
) {
    @Embedded
    var auditMetadata: AuditMetadata = AuditMetadata()

    fun toDomain(): Organization =
        Organization(
            id = OrganizationId(id),
            name = Name(name),
            industry = Industry(industry),
            size = Size(size),
            ownerId = OwnerId(ownerId),
            maximumAmountOfTeams = MaximumAmountOfTeams(maximumAmountOfTeams)
        )
}

fun Organization.toJpa(): JpaOrganization =
    JpaOrganization(
        id = id.uuid,
        name = name.string,
        industry = industry.string,
        size = size.string,
        ownerId = ownerId.string,
        maximumAmountOfTeams = maximumAmountOfTeams.int
    )
