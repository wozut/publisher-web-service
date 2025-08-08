package tcla.contexts.tcla.core.domain.team.rule

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.authentication.core.RequestInfo
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamId

@Named
class RequesterOwnsTeamRule(
    private val teamRepository: TeamRepository
) {
    fun ensure(
        team: Team,
        requesterId: String
    ): Either<Failure, Unit> =
        when (team.ownerId.string) {
            requesterId -> Unit.right()
            else -> Failure.RequesterDoesNotOwnResource.Team.left()
        }

    fun ensure(
        teamId: TeamId
    ): Either<NonEmptyList<Failure>, Team> =
        teamRepository.find(teamId)
            .flatMap { team ->
                when (val requesterId = RequestInfo.getRequesterId()) {
                    null -> Failure.RequesterDoesNotOwnResource.Team.nel().left()
                    else -> when (team.ownerId.string) {
                        requesterId -> team.right()
                        else -> Failure.RequesterDoesNotOwnResource.Team.nel().left()
                    }
                }
            }
}
