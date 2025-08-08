package tcla.contexts.tcla.core.application.teammember.delete

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.teammember.delete.DeleteTeamMember
import tcla.contexts.tcla.core.domain.teammember.model.TeamMemberId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid

@Named
class DeleteTeamMemberCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val deleteTeamMember: DeleteTeamMember
) {
    fun execute(command: DeleteTeamMemberCommand): Either<NonEmptyList<Failure>, DeleteTeamMemberSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            ensureRequestIsAuthenticated()
                .flatMap { requesterId ->
                    command.id.toUuid()
                        .mapLeft { _: StringNotConformsUuid -> nonEmptyListOf(Failure.StringIsNotUuid.TeamMemberId) }
                        .flatMap { uuid -> deleteTeamMember.execute(TeamMemberId(uuid), requesterId) }
                        .flatMap { DeleteTeamMemberSuccess.right() }
                }
        }

    private fun ensureRequestIsAuthenticated(): Either<NonEmptyList<Failure>, String> =
        requestIsAuthenticatedRule.ensure()
            .mapLeft { nonEmptyListOf(Failure.RequestNotAuthenticated) }
}
