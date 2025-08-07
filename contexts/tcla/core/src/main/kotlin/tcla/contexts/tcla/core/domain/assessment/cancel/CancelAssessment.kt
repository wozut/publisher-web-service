package tcla.contexts.tcla.core.domain.assessment.cancel

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.StepFailure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.cancel.scheduleassessmentcancellationmessagetoaccount.ScheduleAssessmentCancellationMessageToAccount
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.assessment.rule.RequesterOwnsAssessmentRule
import tcla.contexts.tcla.core.domain.message.accountmessage.AccountMessageRepository
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.teammembermessage.TeamMemberMessageRepository
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class CancelAssessment(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val requesterOwnsAssessmentRule: RequesterOwnsAssessmentRule,
    private val teamMemberMessageRepository: TeamMemberMessageRepository,
    private val accountMessageRepository: AccountMessageRepository,
    private val scheduleAssessmentCancellationMessageToAccount: ScheduleAssessmentCancellationMessageToAccount,
    private val assessmentRepository: AssessmentRepository
) {
    fun execute(assessment: Assessment): Either<NonEmptyList<Failure>, Assessment> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            ensureRequestIsAuthenticated()
                .flatMap { requesterId -> ensureRequesterOwnsAssessment(assessment, requesterId) }
                .flatMap {
                    ensureSurveyExists(assessment)
                        .flatMap { surveyId ->
                            deleteNotSentYetTeamMemberMessages(surveyId)
                                .flatMap { deleteNotSentYetAccountMessages(surveyId) }
                                .flatMap { scheduleAssessmentCancellationMessageToTeamOwner(assessment, surveyId) }
                        }
                }.flatMap { cancelAssessment(assessment) }
                .flatMap { updatedAssessment -> assessmentRepository.save(updatedAssessment) }
        }

    private fun scheduleAssessmentCancellationMessageToTeamOwner(
        assessment: Assessment,
        surveyId: QuestionnaireId
    ): Either<NonEmptyList<Failure>, Unit> =
        scheduleAssessmentCancellationMessageToAccount.execute(
            assessmentTitle = assessment.title,
            teamId = assessment.teamId,
            teamName = assessment.teamName,
            surveyId = surveyId
        )

    private fun ensureSurveyExists(assessment: Assessment): Either<NonEmptyList<Failure>, QuestionnaireId> =
        when (val surveyId = assessment.questionnaireId) {
            null -> nonEmptyListOf(Failure.DataWasExpectedToExist.Survey).left()
            else -> surveyId.right()
        }

    private fun cancelAssessment(assessment: Assessment): Either<NonEmptyList<Failure>, Assessment> =
        assessment.stepForwardTo(Step.Canceled)
            .mapLeft { failure: StepFailure -> nonEmptyListOf(failure) }

    private fun ensureRequesterOwnsAssessment(
        assessment: Assessment,
        requesterId: String
    ): Either<NonEmptyList<Failure>, AssessmentId> =
        requesterOwnsAssessmentRule.ensure(assessment = assessment, requesterId = requesterId)

    private fun ensureRequestIsAuthenticated(): Either<NonEmptyList<Failure>, String> =
        requestIsAuthenticatedRule.ensure()

    private fun deleteNotSentYetTeamMemberMessages(surveyId: QuestionnaireId): Either<NonEmptyList<Failure>, Unit> =
        teamMemberMessageRepository.searchBySurveyIdAndActuallySentAtIsNull(surveyId)
            .flatMap { teamMemberMessages ->
                teamMemberMessages.filter { teamMemberMessage ->
                    teamMemberMessage.type in setOf(
                        TeamMemberMessage.Type.SURVEY_DURATION_EXTENDED,
                        TeamMemberMessage.Type.SURVEY_INVITATION,
                        TeamMemberMessage.Type._48_HOURS_BEFORE_SURVEY_END
                    )
                }.map { it.id }.let { teamMemberMessageIdsForDeletion ->
                    teamMemberMessageRepository.deleteAll(teamMemberMessageIdsForDeletion)
                        .mapLeft { nonEmptyListOf(it) }
                }
            }

    private fun deleteNotSentYetAccountMessages(surveyId: QuestionnaireId): Either<NonEmptyList<Failure>, Unit> =
        accountMessageRepository.searchBySurveyIdAndActuallySentAtIsNull(surveyId)
            .flatMap { accountMessages ->
                accountMessages.filter { accountMessage ->
                    accountMessage.type in setOf(
                        AccountMessage.Type._48_HOURS_BEFORE_SURVEY_END
                    )
                }.map { it.id }.let { accountMessageIdsForDeletion ->
                    accountMessageRepository.deleteAll(accountMessageIdsForDeletion)
                        .mapLeft { nonEmptyListOf(it) }
                }
            }
}


