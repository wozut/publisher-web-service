package tcla.contexts.analytics.onboarding

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaOnboardingRepository : JpaRepository<JpaOnboarding, UUID> {
    fun findAllByOwnerId(ownerId: String): List<JpaOnboarding>
    fun existsByOwnerId(ownerId: String): Boolean
}
