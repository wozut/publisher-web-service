package tcla.contexts.tcla.core.domain.team.model

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import tcla.contexts.tcla.core.application.failures.TeamFailure
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.contexts.tcla.core.domain.timezone.isValidTimeZone
import java.util.TimeZone

data class Team private constructor(
    val id: TeamId,
    val name: Name,
    val timeZone: TimeZone,
    val ownerId: TeamOwnerId,
    val organizationId: OrganizationId,
    val isArchived: Boolean
) {
    companion object {
        operator fun invoke(
            id: TeamId,
            name: String,
            timeZone: String,
            ownerId: TeamOwnerId,
            organizationId: OrganizationId,
            isArchived: Boolean
        ): Either<NonEmptyList<TeamFailure>, Team> = either {
            zipOrAccumulate(
                { validateName(name) },
                { validateTimeZone(timeZone) }
            ) { _, _ ->
                Team(
                    id = id,
                    name = Name(name),
                    timeZone = TimeZone.getTimeZone(timeZone),
                    ownerId = ownerId,
                    organizationId = organizationId,
                    isArchived = isArchived
                )
            }
        }

        private fun Raise<TeamFailure>.validateName(newName: String) {
            ensure(newName.isNotBlank()) { TeamFailure.InvalidName }
        }

        private fun Raise<TeamFailure>.validateTimeZone(timeZone: String) {
            ensure(isValidTimeZone(timeZone)) { TeamFailure.InvalidTimeZone }
        }
    }

    fun updateName(newName: String): Either<TeamFailure, Team> = either {
        validateName(newName)
        this@Team.copy(name = Name(newName))
    }

    fun updateTimeZone(newTimeZone: String): Either<TeamFailure, Team> = either {
        validateTimeZone(newTimeZone)
        this@Team.copy(timeZone = TimeZone.getTimeZone(newTimeZone))
    }

    fun updateIsArchived(newIsArchived: Boolean): Either<TeamFailure, Team> = either {
        this@Team.copy(isArchived = newIsArchived)
    }
}
