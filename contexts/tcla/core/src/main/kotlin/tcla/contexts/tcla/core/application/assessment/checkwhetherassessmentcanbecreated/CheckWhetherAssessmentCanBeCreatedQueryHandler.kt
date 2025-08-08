package tcla.contexts.tcla.core.application.assessment.checkwhetherassessmentcanbecreated

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.checkwhetherassessmentcanbecreated.CheckWhetherAssessmentCanBeCreated
import tcla.contexts.tcla.core.domain.findearliestavailabledateafterlatestassessment.FindEarliestAvailableDateAfterLatestAssessment
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.team.rule.RequesterOwnsTeamRule
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid

@Named
class CheckWhetherAssessmentCanBeCreatedQueryHandler(
    private val transactionExecutor: TransactionExecutor,
    private val checkWhetherAssessmentCanBeCreated: CheckWhetherAssessmentCanBeCreated,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val requesterOwnsTeamRule: RequesterOwnsTeamRule,
    private val findEarliestAvailableDateAfterLatestAssessment: FindEarliestAvailableDateAfterLatestAssessment
) {
    fun execute(query: CheckWhetherAssessmentCanBeCreatedQuery): Either<NonEmptyList<Failure>, CheckWhetherAssessmentCanBeCreatedSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            requestIsAuthenticatedRule.ensure()
                .flatMap {
                    query.teamId.toUuid()
                        .mapLeft { Failure.StringIsNotUuid.TeamId.nel() }
                }
                .flatMap { teamUuid -> TeamId(teamUuid).right() }
                .flatMap { teamId: TeamId ->
                    requesterOwnsTeamRule.ensure(teamId)
                        .flatMap { _ -> checkWhetherAssessmentCanBeCreated.execute(teamId = teamId) }
                        .flatMap { result: Boolean ->
                            findEarliestAvailableDateAfterLatestAssessment.execute(teamId = teamId)
                                .flatMap { date ->
                                    CheckWhetherAssessmentCanBeCreatedSuccess(
                                        result = result,
                                        earliestAvailableDateAfterLatestAssessment = date
                                    ).right()
                                }
                        }
                }
        }
}
