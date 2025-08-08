package tcla.contexts.tcla.core.domain.scheduleresultsavailablemessage

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.Title
import tcla.contexts.tcla.core.domain.extractFirstName
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.accountmessage.AccountMessageRepository
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.team.TeamRepository
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
class ScheduleResultsAvailableMessage(
    private val transactionExecutor: TransactionExecutor,
    private val accountMessageRepository: AccountMessageRepository,
    private val teamRepository: TeamRepository,
    private val findTeamOwner: FindTeamOwner,
//    @Value("\${web-application.base-url}") private val webApplicationBaseUrl: String,
//    @Value("\${web-application.report-path}") private val webApplicationReportPath: String,
    private val assessmentRepository: AssessmentRepository
) {
    fun execute(
        assessmentId: AssessmentId,
        teamId: TeamId,
        questionnaireId: QuestionnaireId
    ): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            teamRepository.find(teamId)
                .flatMap { team ->
                    findTeamOwner(team.ownerId)
                        .flatMap { account ->
                            assessmentRepository.find(assessmentId)
                                .flatMap { assessment ->
                                    AccountMessage(
                                        id = AccountMessageId(UUID.randomUUID()),
                                        channel = Channel.EMAIL,
                                        type = AccountMessage.Type.RESULTS_AVAILABLE,
                                        scheduledToBeSentAt = Instant.now(),
                                        actuallySentAt = null,
                                        accountId = team.ownerId.string,
                                        surveyId = questionnaireId,
                                        extraData = buildResultsAvailableExtraData(
                                            assessmentTitle = assessment.title,
                                            teamOwnerName = extractFirstName(account.name.string),
                                            teamName = assessment.teamName,
                                            assessmentId = assessmentId
                                        )
                                    ).right()
                                }
                        }
                }.flatMap {
                    //TODO ensure message not exists yet
                    it.right()
                }.flatMap { accountMessage -> accountMessageRepository.saveAll(listOf(accountMessage)) }
                .flatMap { it.right() }
        }

    private fun buildResultsAvailableExtraData(
        assessmentTitle: Title,
        teamOwnerName: String,
        teamName: String,
        assessmentId: AssessmentId
    ): Hashtable<String, String> =
        Hashtable(
            hashMapOf(
                Pair("assessmentTitle", assessmentTitle.string),
                Pair("surveyOwnerName", teamOwnerName),
                Pair("teamName", teamName),
                Pair("productName", PRODUCT_NAME),
//                Pair("resultsUrl", "${webApplicationBaseUrl}${webApplicationReportPath}/${assessmentId.uuid}"),
            )
        )

    private fun findTeamOwner(teamOwnerId: TeamOwnerId): Either<NonEmptyList<Failure>, TeamOwner> =
        findTeamOwner.execute(teamOwnerId)
            .mapLeft { failure ->
                nonEmptyListOf(failure)
            }
}
