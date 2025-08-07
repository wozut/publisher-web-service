package tcla.contexts.tcla.core.application

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule

@Named
class RequesterIsSuperAdminRule(
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule
) {
    fun ensure(): Either<NonEmptyList<Failure>, Unit> =
        requestIsAuthenticatedRule.ensure()
            .flatMap { requesterId ->
                when (requesterId) {
                    in SuperAdminUsers.collection -> Unit.right()
                    else -> Failure.InsufficientPermissions.nel().left()
                }
            }
}