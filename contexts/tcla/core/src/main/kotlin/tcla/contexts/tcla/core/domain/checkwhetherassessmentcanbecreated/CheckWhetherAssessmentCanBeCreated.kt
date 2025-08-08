package tcla.contexts.tcla.core.domain.checkwhetherassessmentcanbecreated

import arrow.core.Either
import arrow.core.NonEmptyList
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.rule.CheckWhetherTeamSizeIsBigEnough
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class CheckWhetherAssessmentCanBeCreated(
    private val transactionExecutor: TransactionExecutor,
    private val checkWhetherTeamSizeIsBigEnough: CheckWhetherTeamSizeIsBigEnough
) {
    fun execute(teamId: TeamId): Either<NonEmptyList<Failure>, Boolean> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            checkWhetherTeamSizeIsBigEnough.execute(teamId)
        }
}
