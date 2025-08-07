package tcla.contexts.tcla.core.domain.schedulemessagesforsurvey.account

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Title
import tcla.contexts.tcla.core.domain.extractFirstName
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.accountmessage.AccountMessageRepository
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teamowner.find.FindTeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.time.Duration
import java.util.Hashtable
import java.util.UUID

@Named
class ScheduleAccountMessagesForSurvey(
    private val transactionExecutor: TransactionExecutor,
    private val accountMessageRepository: AccountMessageRepository,
    private val teamRepository: TeamRepository,
    private val findTeamOwner: FindTeamOwner,
//    @Value("\${web-application.base-url}") private val webApplicationBaseUrl: String,
//    @Value("\${web-application.dashboard-path}") private val webApplicationDashboardPath: String,
    private val assessmentRepository: AssessmentRepository
) {
    fun execute(
        teamId: TeamId,
        questionnaire: Questionnaire
    ): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            val instantFor48HoursBeforeSurveyEnd =
                questionnaire.responseAcceptanceInterval.end.minus(Duration.ofHours(48))

            teamRepository.find(teamId)
                .flatMap { team ->
                    findTeamOwner(team.ownerId)
                        .flatMap { account ->
                            val messages: MutableList<AccountMessage> = mutableListOf()

                            if (instantFor48HoursBeforeSurveyEnd > questionnaire.responseAcceptanceInterval.start) {
                                assessmentRepository.find(questionnaire.assessmentId)
                                    .flatMap { assessment ->
                                        val reminder48Hours = AccountMessage(
                                            id = AccountMessageId(UUID.randomUUID()),
                                            channel = Channel.EMAIL,
                                            type = AccountMessage.Type._48_HOURS_BEFORE_SURVEY_END,
                                            scheduledToBeSentAt = instantFor48HoursBeforeSurveyEnd,
                                            actuallySentAt = null,
                                            accountId = team.ownerId.string,
                                            surveyId = questionnaire.id,
                                            extraData = build48HoursBeforeSurveyEndExtraData(
                                                teamOwnerName = extractFirstName(account.name.string),
                                                assessmentTitle = assessment.title,
                                                teamName = assessment.teamName
                                            )
                                        )
                                        messages.add(reminder48Hours)
                                        Unit.right()
                                    }

                            }
                            messages.right()
                        }
                }.flatMap {
                    //TODO ensure uuids are unique
                    it.right()
                }.flatMap {
                    //TODO ensure message not exists yet
                    it.right()
                }.flatMap { accountMessageRepository.saveAll(it) }
                .flatMap { it.right() }
        }

    private fun findTeamOwner(teamOwnerId: TeamOwnerId): Either<NonEmptyList<Failure>, TeamOwner> =
        findTeamOwner.execute(teamOwnerId).mapLeft { nonEmptyListOf(it) }

    private fun build48HoursBeforeSurveyEndExtraData(
        assessmentTitle: Title,
        teamOwnerName: String,
        teamName: String
    ): Hashtable<String, String> =
        Hashtable(
            hashMapOf(
                Pair("surveyOwnerName", teamOwnerName),
                Pair("assessmentTitle", assessmentTitle.string),
                Pair("teamName", teamName),
                Pair("productName", PRODUCT_NAME),
//                Pair("appDashboardUrl", "${webApplicationBaseUrl}${webApplicationDashboardPath}")
            )
        )
}
