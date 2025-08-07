package tcla.contexts.tcla.core.domain.organization.rule

import arrow.core.*
import jakarta.inject.Named
import tcla.contexts.authentication.core.RequestInfo
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.organization.OrganizationRepository
import tcla.contexts.tcla.core.domain.organization.model.Organization
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId

@Named
class RequesterOwnsOrganizationRule(
    private val organizationRepository: OrganizationRepository
) {
    fun ensure(
        organizationId: OrganizationId
    ): Either<NonEmptyList<Failure>, OrganizationId> =
        RequestInfo.getRequesterId()
            .let { requesterId: String? ->
                when (requesterId) {
                    null -> Failure.RequestNotAuthenticated.nel().left()
                    else -> ensure(organizationId, requesterId)
                }
            }

    private fun ensure(
            organization: Organization,
            requesterId: String
    ): Either<NonEmptyList<Failure>, OrganizationId> =
            when (organization.ownerId.string) {
                requesterId -> organization.id.right()
                else -> nonEmptyListOf(Failure.RequesterDoesNotOwnResource.Organization).left()
            }

    private fun ensure(
        organizationId: OrganizationId,
        requesterId: String
    ): Either<NonEmptyList<Failure>, OrganizationId> =
        organizationRepository.find(organizationId)
            .flatMap { organization -> ensure(organization, requesterId) }
}
