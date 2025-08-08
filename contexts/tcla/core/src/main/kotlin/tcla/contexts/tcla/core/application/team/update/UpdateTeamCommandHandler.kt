package tcla.contexts.tcla.core.application.team.update

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.TeamFailure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.cancelteamassessments.CancelTeamAssessments
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.team.rule.RequesterOwnsTeamRule
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid

@Named
class UpdateTeamCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val teamRepository: TeamRepository,
    private val requesterOwnsTeamRule: RequesterOwnsTeamRule,
    private val cancelTeamAssessments: CancelTeamAssessments
) {
    fun execute(command: UpdateTeamCommand): Either<NonEmptyList<Failure>, UpdateTeamSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            ensureRequestIsAuthenticated()
                .flatMap { requesterId ->
                    command.id.toUuid()
                        .mapLeft { nonEmptyListOf(Failure.StringIsNotUuid.TeamId) }
                        .flatMap { uuid -> teamRepository.find(TeamId(uuid)) }
                        .flatMap { team -> ensureRequesterOwnsTeam(team, requesterId) }
                        .flatMap { team -> updateTeam(team, command.fields) }
                        .flatMap { updatedTeam -> teamRepository.save(updatedTeam)
                            .mapLeft { nonEmptyListOf(it) } }
                        .flatMap { UpdateTeamSuccess.right() }
                }
        }

    private fun ensureRequesterOwnsTeam(
        team: Team,
        requesterId: String
    ): Either<NonEmptyList<Failure>, Team> = requesterOwnsTeamRule.ensure(team, requesterId)
        .mapLeft { nonEmptyListOf(it) }
        .flatMap { team.right() }

    private fun ensureRequestIsAuthenticated(): Either<NonEmptyList<Failure>, String> =
        requestIsAuthenticatedRule.ensure()

    private fun updateTeam(
        team: Team,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Team> =
        updateName(team, fieldsToUpdate)
            .flatMap { updatedTeam -> updateTimeZone(updatedTeam, fieldsToUpdate) }
            .flatMap { updatedTeam -> updateIsArchived(updatedTeam, fieldsToUpdate) }


    private fun updateName(
        team: Team,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Team> {
        val key = "name"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val name = fieldsToUpdate[key]) {
                null -> nonEmptyListOf(TeamFailure.InvalidName).left()
                else -> when(name) {
                    is String ->team.updateName(name).mapLeft { nonEmptyListOf(it) }
                    else -> nonEmptyListOf(TeamFailure.InvalidName).left()
                }
            }

            false -> team.right()
        }
    }

    private fun updateIsArchived(
        team: Team,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Team> {
        val key = "isArchived"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> TeamFailure.InvalidIsArchived.nel().left()
                else -> when(newValue){
                    is Boolean -> {
                        cancelAssessmentsIfNeeded(team.id, team.isArchived, newValue)
                            .flatMap { team.updateIsArchived(newValue).mapLeft { it.nel() } }
                    }
                    else -> TeamFailure.InvalidIsArchived.nel().left()
                }
            }

            false -> team.right()
        }
    }

    private fun cancelAssessmentsIfNeeded(teamId: TeamId, isCurrentlyArchived: Boolean, isGoingToBeArchived: Boolean): Either<NonEmptyList<Failure>, Unit> =
        when(!isCurrentlyArchived && isGoingToBeArchived) {
            true -> cancelTeamAssessments.execute(teamId)
            false -> Unit.right()
        }

    private fun updateTimeZone(
        team: Team,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Team> {
        val key = "timeZone"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val timeZone = fieldsToUpdate[key]) {
                null -> nonEmptyListOf(TeamFailure.InvalidTimeZone).left()
                else -> when(timeZone) {
                    is String -> team.updateTimeZone(timeZone)
                        .mapLeft { nonEmptyListOf(it) }
                    else -> nonEmptyListOf(TeamFailure.InvalidTimeZone).left()
                }
            }

            false -> team.right()
        }
    }
}
