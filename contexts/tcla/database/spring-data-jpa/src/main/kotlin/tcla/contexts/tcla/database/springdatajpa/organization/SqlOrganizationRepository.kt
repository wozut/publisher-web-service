package tcla.contexts.tcla.database.springdatajpa.organization

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.organization.OrganizationFilterKey
import tcla.contexts.tcla.core.domain.organization.OrganizationRepository
import tcla.contexts.tcla.core.domain.organization.model.Organization
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.contexts.tcla.core.domain.organization.model.OwnerId
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator

@Named
class SqlOrganizationRepository(
    private val jpaOrganizationRepository: JpaOrganizationRepository
) : OrganizationRepository {
    override fun exists(id: OrganizationId): Either<NonEmptyList<Failure>, Boolean> =
        Either.catch { jpaOrganizationRepository.existsById(id.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { exists -> exists.right() }

    override fun save(organization: Organization): Either<NonEmptyList<Failure>, OrganizationId> =
        Either.catch { jpaOrganizationRepository.save(organization.toJpa()) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaOrganization -> OrganizationId(jpaOrganization.id).right() }

    override fun search(filter: Filter<OrganizationFilterKey>?): Either<NonEmptyList<Failure>, List<Organization>> =
        findJpaOrganizations(filter)
            .flatMap { jpaOrganizations: List<JpaOrganization> ->
                jpaOrganizations.map { jpaOrganization -> jpaOrganization.toDomain() }.right()
            }

    override fun find(id: OrganizationId): Either<NonEmptyList<Failure>, Organization> =
        Either.catch { jpaOrganizationRepository.findByIdOrNull(id.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaOrganization: JpaOrganization? ->
                when (jpaOrganization) {
                    null -> Failure.EntityNotFound.Organization.nel().left()
                    else -> jpaOrganization.toDomain().right()
                }
            }

    private fun findJpaOrganizations(filter: Filter<OrganizationFilterKey>?): Either<NonEmptyList<Failure>, List<JpaOrganization>> =
        when (filter) {
            null -> Either.catch { jpaOrganizationRepository.findAll() }
                .mapLeft { throwable -> Failure.DatabaseException(exception = throwable).nel() }

            else -> when (filter) {
                is ManyValuesFilter<OrganizationFilterKey, *> -> Failure.UnsupportedDatabaseFilter.Organization.nel().left()

                is OneValueFilter<OrganizationFilterKey, *> -> when (filter.key) {
                    OrganizationFilterKey.OWNER -> when (val value = filter.value) {
                        is OwnerId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaOrganizationRepository.findAllByOwnerId(value.string)
                            }.mapLeft { throwable -> Failure.DatabaseException(throwable).nel() }

                            Operator.NaryOperator.In -> Failure.UnsupportedDatabaseFilter.Organization.nel().left()
                        }

                        else -> Failure.UnsupportedDatabaseFilter.Organization.nel().left()
                    }
                }
            }
        }
}
