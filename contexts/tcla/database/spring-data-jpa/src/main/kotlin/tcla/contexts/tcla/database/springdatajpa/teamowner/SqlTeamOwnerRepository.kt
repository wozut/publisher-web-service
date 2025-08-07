package tcla.contexts.tcla.database.springdatajpa.teamowner

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.accounts.core.application.failures.FindAccountFailure
import tcla.contexts.accounts.core.application.account.find.FindAccountQueryHandler
import tcla.contexts.accounts.core.domain.account.model.Account
import tcla.contexts.accounts.core.domain.account.model.AccountId
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.SearchTeamOwnersFailure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.teamowner.TeamOwnerFilterKey
import tcla.contexts.tcla.core.domain.teamowner.TeamOwnerRepository
import tcla.contexts.tcla.core.domain.teamowner.model.Email
import tcla.contexts.tcla.core.domain.teamowner.model.Name
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessmentRepository
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator

@Named
class SqlTeamOwnerRepository(
    private val jpaAssessmentRepository: JpaAssessmentRepository,
    private val findAccountQueryHandler: FindAccountQueryHandler
) : TeamOwnerRepository {
    override fun find(id: TeamOwnerId): Either<Failure, TeamOwner> =
        findAccountQueryHandler.find(AccountId(id.string))
            .mapLeft { findAccountFailure ->
                when (findAccountFailure) {
                    FindAccountFailure.NotFound -> Failure.EntityNotFound.TeamOwner
                    FindAccountFailure.RequestNotAuthenticated -> Failure.RequestNotAuthenticated
                    is FindAccountFailure.TechnicalException -> Failure.TechnicalException(findAccountFailure.exception)
                    FindAccountFailure.Unauthorized -> Failure.RequesterDoesNotOwnResource.TeamOwner
                }
            }.flatMap { account: Account -> account.toTeamOwner().right() }

    override fun search(filter: Filter<TeamOwnerFilterKey>): Either<Failure, List<TeamOwner>> =
        when (filter) {
            is ManyValuesFilter<TeamOwnerFilterKey, *> -> SearchTeamOwnersFailure.UnsupportedDatabaseFilter.left()

            is OneValueFilter<TeamOwnerFilterKey, *> -> when (filter.key) {
                TeamOwnerFilterKey.ASSESSMENT -> when (val value = filter.value) {
                    is AssessmentId -> when (filter.operator) {
                        Operator.BinaryOperator.Equal -> Either.catch {
                            jpaAssessmentRepository.findByIdOrNull(value.uuid)
                        }.mapLeft { throwable -> SearchTeamOwnersFailure.DatabaseException(throwable) }
                            .flatMap { assessment ->
                                when (assessment) {
                                    null -> Failure.EntityNotFound.Assessment.left()
                                    else -> this.find(TeamOwnerId(assessment.team.ownerId))
                                        .fold(
                                            { it.left() },
                                            { teamOwner -> listOf(teamOwner).right() }
                                        )
                                }
                            }

                        Operator.NaryOperator.In -> SearchTeamOwnersFailure.UnsupportedDatabaseFilter.left()
                    }

                    else -> SearchTeamOwnersFailure.UnsupportedDatabaseFilter.left()
                }
            }
        }

    private fun Account.toTeamOwner(): TeamOwner =
        TeamOwner(
            id = TeamOwnerId(id.string),
            name = Name(name.string),
            email = Email(email.string)
        )
}


