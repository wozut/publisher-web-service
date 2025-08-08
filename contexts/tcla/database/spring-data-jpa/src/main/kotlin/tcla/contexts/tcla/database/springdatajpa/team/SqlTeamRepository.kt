package tcla.contexts.tcla.database.springdatajpa.team

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flattenOrAccumulate
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.team.TeamFilterKey
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.contexts.tcla.database.springdatajpa.organization.JpaOrganizationRepository
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator

@Named
class SqlTeamRepository(
    private val jpaTeamRepository: JpaTeamRepository,
    private val jpaOrganizationRepository: JpaOrganizationRepository
) : TeamRepository {
    override fun exists(id: TeamId): Either<Failure, Boolean> =
        Either.catch { jpaTeamRepository.existsById(id.uuid) }
            .mapLeft(Failure::DatabaseException)
            .flatMap { exists -> exists.right() }

    override fun search(filter: Filter<TeamFilterKey>?): Either<NonEmptyList<Failure>, List<Team>> =
        findJpaTeams(filter)
            .flatMap { jpaTeams: List<JpaTeam> ->
                jpaTeams
                    .map { jpaTeam -> jpaTeam.toDomain() }
                    .flattenOrAccumulate()
            }

    override fun find(teamId: TeamId): Either<NonEmptyList<Failure>, Team> =
        Either.catch { jpaTeamRepository.findByIdOrNull(teamId.uuid) }
            .mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
            .flatMap { jpaTeam: JpaTeam? ->
                when (jpaTeam) {
                    null -> nonEmptyListOf(Failure.EntityNotFound.Team).left()
                    else -> jpaTeam.toDomain()
                }
            }

    override fun delete(id: TeamId): Either<Failure, Unit> =
        Either.catch { jpaTeamRepository.deleteById(id.uuid) }
            .mapLeft { Failure.DatabaseException(it) }

    override fun save(team: Team): Either<Failure, TeamId> =
        Either.catch { jpaOrganizationRepository.findByIdOrNull(team.organizationId.uuid) }
            .mapLeft { throwable -> Failure.DatabaseException(throwable) }
            .flatMap { jpaOrganization ->
                when (jpaOrganization) {
                    null -> Failure.DataWasExpectedToExist.Organization.left()
                    else -> jpaOrganization.right()
                }
            }.flatMap { jpaOrganization ->
                Either.catch { jpaTeamRepository.save(team.toJpa(jpaOrganization)) }
                    .mapLeft(Failure::DatabaseException)
            }
            .flatMap { jpaTeam -> TeamId(jpaTeam.id).right() }

    private fun findJpaTeams(filter: Filter<TeamFilterKey>?): Either<NonEmptyList<Failure>, List<JpaTeam>> =
        when (filter) {
            null -> Either.catch { jpaTeamRepository.findAll() }
                .mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(exception = throwable)) }

            else -> when (filter) {
                is ManyValuesFilter<TeamFilterKey, *> -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Team).left()

                is OneValueFilter<TeamFilterKey, *> -> when (filter.key) {
                    TeamFilterKey.OWNER -> when (val value = filter.value) {
                        is TeamOwnerId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaTeamRepository.findAllByOwnerId(value.string)
                            }.mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(throwable)) }

                            Operator.NaryOperator.In -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Team).left()
                        }

                        else -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Team).left()
                    }

                    TeamFilterKey.ORGANIZATION -> when (val value = filter.value) {
                        is OrganizationId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaTeamRepository.findAllByOrganization_Id(value.uuid)
                            }.mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(throwable)) }

                            Operator.NaryOperator.In -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Team).left()
                        }

                        else -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Team).left()
                    }
                }
            }
        }
}
