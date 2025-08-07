package tcla.contexts.analytics.onboarding

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity(name = "onboarding")
@Table(name = "onboarding", schema = "analytics")
data class JpaOnboarding(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val userHasSeenDemo: Boolean,
    @Column(nullable = false)
    val ownerId: String
)
