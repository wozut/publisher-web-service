package tcla.contexts.tcla.core.domain.assessment.rule

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teammember.TeamMemberFilterKey
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.libraries.search.OneValueFilter

@Named
class CheckWhetherTeamSizeIsBigEnough(
    private val teamMemberRepository: TeamMemberRepository,
    private val teamRepository: TeamRepository,
) {
    fun execute(team: Team): Either<NonEmptyList<Failure>, Boolean> =
        teamMemberRepository.search(OneValueFilter(TeamMemberFilterKey.TEAM, team.id))
            .mapLeft { failure -> failure.nel() }
            .flatMap { teamMembers ->
                when (teamMembers.size) {
                    in 0..2 -> false
                    else -> true
                }.right()
            }

    fun execute(teamId: TeamId): Either<NonEmptyList<Failure>, Boolean> =
        teamRepository.find(teamId)
            .flatMap { team -> execute(team) }
}
