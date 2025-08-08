package tcla.contexts.tcla.core.domain.team.model


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.application.failures.TeamFailure
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import java.util.TimeZone
import java.util.UUID

class TeamTest {
    @Test
    fun `name validation on creation`() {
        Team(
            id = TeamId(UUID.randomUUID()),
            name = "",
            timeZone = TimeZone.getAvailableIDs().first(),
            ownerId = TeamOwnerId("team owner id"),
            organizationId = OrganizationId(UUID.randomUUID()),
            isArchived = false
        ).fold(
            { failures: NonEmptyList<TeamFailure> ->
                assertThat(failures).hasSize(1)
                assertThat(failures.head).isExactlyInstanceOf(TeamFailure.InvalidName::class.java)
            },
            { Assertions.fail("It must be left") }
        )
    }

    @Test
    fun `time zone validation on creation`() {
        Team(
            id = TeamId(UUID.randomUUID()),
            name = "Team name",
            timeZone = invalidTimeZone(),
            ownerId = TeamOwnerId("team owner id"),
            organizationId = OrganizationId(UUID.randomUUID()),
            isArchived = false
        ).fold(
            { failures: NonEmptyList<TeamFailure> ->
                assertThat(failures).hasSize(1)
                assertThat(failures.head).isExactlyInstanceOf(TeamFailure.InvalidTimeZone::class.java)
            },
            { Assertions.fail("It must be left") }
        )
    }

    @Test
    fun `name validation on update`() {
        validTeam()
            .flatMap { team -> team.updateName("") }
            .mapLeft { failure -> nonEmptyListOf(failure) }
            .fold(
                { failures ->
                    assertThat(failures).hasSize(1)
                    assertThat(failures.head).isExactlyInstanceOf(TeamFailure.InvalidName::class.java)
                },
                { Assertions.fail("It must be left") }
            )
    }

    @Test
    fun `time zone validation on update`() {
        validTeam()
            .flatMap { team -> team.updateTimeZone(invalidTimeZone()) }
            .mapLeft { failure -> nonEmptyListOf(failure) }
            .fold(
                { failures ->
                    assertThat(failures).hasSize(1)
                    assertThat(failures.head).isExactlyInstanceOf(TeamFailure.InvalidTimeZone::class.java)
                },
                { Assertions.fail("It must be left") }
            )
    }

    private fun invalidTimeZone() = "%&-.Invalid time zone.-&%"

    private fun validTeam(): Either<NonEmptyList<TeamFailure>, Team> = Team(
        id = TeamId(UUID.randomUUID()),
        name = "Team Name",
        timeZone = TimeZone.getAvailableIDs().first(),
        ownerId = TeamOwnerId("sadsad"),
        organizationId = OrganizationId(UUID.randomUUID()),
        isArchived = false
    )
}
