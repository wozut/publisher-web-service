package tcla.contexts.tcla.core.domain.assessment.cancel.scheduleassessmentcancellationmessagetoaccount

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.assessment.model.Title
import tcla.contexts.tcla.core.domain.extractFirstName
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.accountmessage.AccountMessageRepository
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teamowner.find.FindTeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.time.Instant
import java.util.Hashtable
import java.util.UUID

@Named
class ScheduleAssessmentCancellationMessageToAccount(
    private val transactionExecutor: TransactionExecutor,
    private val accountMessageRepository: AccountMessageRepository,
    private val teamRepository: TeamRepository,
    private val findTeamOwner: FindTeamOwner,
//    @Value("\${web-application.base-url}") private val webApplicationBaseUrl: String,
//    @Value("\${web-application.dashboard-path}") private val webApplicationDashboardPath: String
) {
    fun execute(
        assessmentTitle: Title,
        teamName: String,
        teamId: TeamId,
        surveyId: QuestionnaireId
    ): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            findTeam(teamId)
                .flatMap { team ->
                    findTeamOwner(team.ownerId)
                        .flatMap { account ->
                            AccountMessage(
                                id = AccountMessageId(UUID.randomUUID()),
                                channel = Channel.EMAIL,
                                type = AccountMessage.Type.CANCELED_SURVEY,
                                scheduledToBeSentAt = Instant.now(),
                                actuallySentAt = null,
                                accountId = team.ownerId.string,
                                surveyId = surveyId,
                                extraData = buildResultsAvailableExtraData(
                                    teamOwnerName = extractFirstName(account.name.string),
                                    assessmentTitle = assessmentTitle.string,
                                    teamName = teamName
                                )
                            ).right()
                        }
                }.flatMap {
                    //TODO ensure message not exists yet
                    it.right()
                }.flatMap { accountMessage ->
                    saveAll(accountMessage)
                }.flatMap { it.right() }
        }

    private fun saveAll(accountMessage: AccountMessage): Either<NonEmptyList<Failure>, Unit> =
        accountMessageRepository.saveAll(listOf(accountMessage))

    private fun findTeam(teamId: TeamId): Either<NonEmptyList<Failure>, Team> =
        teamRepository.find(teamId)

    private fun buildResultsAvailableExtraData(
        teamOwnerName: String,
        assessmentTitle: String,
        teamName: String
    ): Hashtable<String, String> =
        Hashtable(
            hashMapOf(
                Pair("surveyOwnerName", teamOwnerName),
                Pair("assessmentTitle", assessmentTitle),
                Pair("teamName", teamName),
                Pair("productName", PRODUCT_NAME),
//                Pair("appDashboardUrl", "${webApplicationBaseUrl}${webApplicationDashboardPath}")
            )
        )

    private fun findTeamOwner(teamOwnerId: TeamOwnerId): Either<NonEmptyList<Failure>, TeamOwner> =
        findTeamOwner.execute(teamOwnerId)
            .mapLeft { it.nel() }
}
