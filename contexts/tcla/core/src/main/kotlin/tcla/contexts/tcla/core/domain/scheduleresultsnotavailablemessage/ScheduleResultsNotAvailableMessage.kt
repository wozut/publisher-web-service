package tcla.contexts.tcla.core.domain.scheduleresultsnotavailablemessage

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.Title
import tcla.contexts.tcla.core.domain.extractFirstName
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.accountmessage.AccountMessageRepository
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
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
class ScheduleResultsNotAvailableMessage(
    private val transactionExecutor: TransactionExecutor,
    private val teamRepository: TeamRepository,
    private val accountMessageRepository: AccountMessageRepository,
    private val findTeamOwner: FindTeamOwner,
    private val questionnaireRepository: QuestionnaireRepository,
//    @Value("\${web-application.base-url}") private val webApplicationBaseUrl: String,
//    @Value("\${web-application.dashboard-path}") private val webApplicationDashboardPath: String
) {
    fun execute(assessment: Assessment, questionnaireId: QuestionnaireId): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            findTeam(assessment.teamId)
                .flatMap { team ->
                    findTeamOwner(team.ownerId)
                        .flatMap { teamOwner ->
                            findQuestionnaire(questionnaireId)
                                .flatMap { questionnaire ->
                                    val type: AccountMessage.Type = AccountMessage.Type.RESULTS_NOT_AVAILABLE
                                    AccountMessage(
                                        id = AccountMessageId(UUID.randomUUID()),
                                        channel = Channel.EMAIL,
                                        scheduledToBeSentAt = Instant.now(),
                                        type = type,
                                        accountId = team.ownerId.string,
                                        surveyId = questionnaireId,
                                        extraData = buildResultsNotAvailableExtraData(
                                            assessmentTitle = assessment.title,
                                            teamOwnerName = extractFirstName(teamOwner.name.string),
                                            teamName = assessment.teamName,
                                            teamSize = questionnaire.responses.maximumAmountToBeCollected.int,
                                            participationPercentageRequired = questionnaire.responses.minimumRateRequired.double * 100.0,
                                            responsesAmountRequired = questionnaire.responses.minimumAmountRequired,
                                            responsesAmountCollected = questionnaire.responses.currentAmountCollected
                                        )
                                    ).right()
                                }
                        }
                }.flatMap { accountMessage ->
                    //TODO ensure message not exists yet
                    accountMessage.right()
                }.flatMap { accountMessage ->
                    accountMessageRepository.saveAll(listOf(accountMessage))
                }
        }

    private fun buildResultsNotAvailableExtraData(
        assessmentTitle: Title,
        teamOwnerName: String,
        teamName: String,
        teamSize: Int,
        participationPercentageRequired: Double,
        responsesAmountRequired: Int,
        responsesAmountCollected: Int,
    ): Hashtable<String, String> =
        Hashtable(
            hashMapOf(
                Pair("assessmentTitle", assessmentTitle.string),
                Pair("surveyOwnerName", teamOwnerName),
                Pair("teamName", teamName),
                Pair("teamSize", teamSize.toString()),
                Pair("participationPercentageRequired", participationPercentageRequired.toString()),
                Pair("responsesAmountRequired", responsesAmountRequired.toString()),
                Pair("responsesAmountCollected", responsesAmountCollected.toString()),
                Pair("productName", PRODUCT_NAME),
//                Pair("appDashboardUrl", "${webApplicationBaseUrl}${webApplicationDashboardPath}")
            )
        )

    private fun findTeamOwner(teamOwnerId: TeamOwnerId): Either<NonEmptyList<Failure>, TeamOwner> =
        findTeamOwner.execute(teamOwnerId).mapLeft { it.nel() }

    private fun findTeam(teamId: TeamId): Either<NonEmptyList<Failure>, Team> =
        teamRepository.find(teamId)

    private fun findQuestionnaire(questionnaireId: QuestionnaireId): Either<NonEmptyList<Failure>, Questionnaire> =
        questionnaireRepository.find(questionnaireId)

}
