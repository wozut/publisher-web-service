package tcla.contexts.tcla.core.domain.organization

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.organization.model.Organization
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId
import tcla.libraries.search.Filter

interface OrganizationRepository {
    fun exists(id: OrganizationId): Either<NonEmptyList<Failure>, Boolean>
    fun save(organization: Organization): Either<NonEmptyList<Failure>, OrganizationId>
    fun search(filter: Filter<OrganizationFilterKey>? = null): Either<NonEmptyList<Failure>, List<Organization>>
    fun find(id: OrganizationId): Either<NonEmptyList<Failure>, Organization>
}
