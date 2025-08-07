package tcla.contexts.tcla.core.domain.schedulemessagesforsurvey

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.schedulemessagesforsurvey.account.ScheduleAccountMessagesForSurvey
import tcla.contexts.tcla.core.domain.schedulemessagesforsurvey.teammember.ScheduleTeamMemberMessagesForSurvey
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class ScheduleMessagesForSurvey(
    private val transactionExecutor: TransactionExecutor,
    private val scheduleTeamMemberMessagesForSurvey: ScheduleTeamMemberMessagesForSurvey,
    private val scheduleAccountMessagesForSurvey: ScheduleAccountMessagesForSurvey
) {
    fun execute(
        teamId: TeamId,
        questionnaire: Questionnaire
    ): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            scheduleTeamMemberMessagesForSurvey.execute(teamId, questionnaire)
                .flatMap { scheduleAccountMessagesForSurvey.execute(teamId, questionnaire) }
        }
}
