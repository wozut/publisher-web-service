package tcla.contexts.tcla.core.domain.schedulemessagesforsurvey.teammember

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flatten
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.PRIVACY_POLICY_URL
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.PRODUCT_URL
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Title
import tcla.contexts.tcla.core.domain.extractFirstName
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.teammembermessage.TeamMemberMessageRepository
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.respondent.RespondentFilterKey
import tcla.contexts.tcla.core.domain.respondent.RespondentRepository
import tcla.contexts.tcla.core.domain.respondent.model.Respondent
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teammember.TeamMemberFilterKey
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.contexts.tcla.core.domain.teammember.model.TeamMember
import tcla.contexts.tcla.core.domain.teammember.model.TeamMemberId
import tcla.contexts.tcla.core.domain.teamowner.find.FindTeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.time.Duration
import java.time.Instant
import java.util.Hashtable
import java.util.UUID

@Named
class ScheduleTeamMemberMessagesForSurvey(
    private val transactionExecutor: TransactionExecutor,
    private val teamMemberRepository: TeamMemberRepository,
    private val teamMemberMessageRepository: TeamMemberMessageRepository,
    private val teamRepository: TeamRepository,
    private val findTeamOwner: FindTeamOwner,
//    @Value("\${web-application.base-url}") private val webApplicationBaseUrl: String,
    private val assessmentRepository: AssessmentRepository,
    private val respondentRepository: RespondentRepository
) {
    fun execute(teamId: TeamId, questionnaire: Questionnaire): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            teamRepository.find(teamId = teamId)
                .flatMap { team -> findTeamOwner(team.ownerId)
                    .flatMap { teamOwner ->
                        teamMemberRepository.search(OneValueFilter(TeamMemberFilterKey.TEAM, teamId))
                            .mapLeft { nonEmptyListOf(it) }
                            .flatMap { teamMembers ->
                                val instantFor48HoursBeforeSurveyEnd =
                                    questionnaire.responseAcceptanceInterval.end.minus(Duration.ofHours(48))
                                assessmentRepository.find(questionnaire.assessmentId)
                                    .flatMap { assessment ->
                                        val filter = OneValueFilter(RespondentFilterKey.ASSESSMENT, questionnaire.assessmentId)
                                        respondentRepository.search(filter)
                                            .flatMap { respondents: List<Respondent> ->
                                                respondents.map { respondent ->
                                                    val teamMember: TeamMember =
                                                        teamMembers.find { teamMember: TeamMember -> teamMember.email.string == respondent.email.string }!!
                                                    scheduleTeamMemberMessagesForOneTeamMember(
                                                        teamName = assessment.teamName,
                                                        assessmentTitle = assessment.title,
                                                        questionnaire = questionnaire,
                                                        teamMemberId = teamMember.id,
                                                        respondentName = respondent.name,
                                                        instantFor48HoursBeforeSurveyEnd = instantFor48HoursBeforeSurveyEnd,
                                                        teamOwnerName = teamOwner.name.string,
                                                        respondentId = respondent.id
                                                    )
                                                }.flatten().right()
                                            }
                                    }
                            }.flatMap {
                                //TODO ensure uuids are unique
                                it.right()
                            }.flatMap {
                                //TODO ensure messages not exists yet
                                it.right()
                            }.flatMap { messages -> teamMemberMessageRepository.saveAll(messages) }
                    }
                }
        }

    private fun scheduleTeamMemberMessagesForOneTeamMember(
        teamName: String,
        assessmentTitle: Title,
        questionnaire: Questionnaire,
        teamMemberId: TeamMemberId,
        instantFor48HoursBeforeSurveyEnd: Instant,
        teamOwnerName: String,
        respondentId: RespondentId,
        respondentName: String
    ): MutableList<TeamMemberMessage> {
        val messages: MutableList<TeamMemberMessage> = mutableListOf()
        val invitationMessage = TeamMemberMessage(
            id = TeamMemberMessageId(UUID.randomUUID()),
            channel = Channel.EMAIL,
            type = TeamMemberMessage.Type.SURVEY_INVITATION,
            scheduledToBeSentAt = questionnaire.responseAcceptanceInterval.start,
            actuallySentAt = null,
            teamMemberId = teamMemberId,
            respondentId = respondentId,
            surveyId = questionnaire.id,
            extraData = buildSurveyInvitationExtraData(
                teamName = teamName,
                teamMemberName = extractFirstName(respondentName),
//                webApplicationBaseUrl = webApplicationBaseUrl,
                questionnaireId = questionnaire.id,
                teamOwnerName = extractFirstName(teamOwnerName),
                respondentId = respondentId
            )
        )
        messages.add(invitationMessage)

        if (instantFor48HoursBeforeSurveyEnd > questionnaire.responseAcceptanceInterval.start) {
            val reminder48Hours = TeamMemberMessage(
                id = TeamMemberMessageId(UUID.randomUUID()),
                channel = Channel.EMAIL,
                type = TeamMemberMessage.Type._48_HOURS_BEFORE_SURVEY_END,
                scheduledToBeSentAt = instantFor48HoursBeforeSurveyEnd,
                actuallySentAt = null,
                teamMemberId = teamMemberId,
                respondentId = respondentId,
                surveyId = questionnaire.id,
                extraData = build48HoursBeforeSurveyEndExtraData(
                    assessmentTitle = assessmentTitle,
                    teamMemberName = extractFirstName(respondentName),
//                    webApplicationBaseUrl = webApplicationBaseUrl,
                    questionnaireId = questionnaire.id,
                    respondentId = respondentId
                )
            )
            messages.add(reminder48Hours)
        }

        return messages
    }

    private fun buildSurveyInvitationExtraData(
        teamName: String,
        teamOwnerName: String,
        teamMemberName: String,
//        webApplicationBaseUrl: String,
        questionnaireId: QuestionnaireId,
        respondentId: RespondentId
    ): Hashtable<String, String> = Hashtable(
        hashMapOf(
            Pair("teamName", teamName),
            Pair("surveyOwnerName", teamOwnerName),
            Pair("teamMemberName", teamMemberName),
//            Pair("surveyUrl", "${webApplicationBaseUrl}/survey/${questionnaireId.uuid}?respondent=${respondentId.uuid}"),
            Pair("productName", PRODUCT_NAME),
            Pair("productUrl", PRODUCT_URL),
            Pair("privacyPolicyUrl", PRIVACY_POLICY_URL)
        )
    )

    private fun build48HoursBeforeSurveyEndExtraData(
        assessmentTitle: Title,
        teamMemberName: String,
//        webApplicationBaseUrl: String,
        questionnaireId: QuestionnaireId,
        respondentId: RespondentId
    ): Hashtable<String, String> =
        Hashtable(
            hashMapOf(
                Pair("assessmentTitle", assessmentTitle.string),
                Pair("teamMemberName", teamMemberName),
//                Pair("surveyUrl", "${webApplicationBaseUrl}/survey/${questionnaireId.uuid}?respondent=${respondentId.uuid}"),
                Pair("productName", PRODUCT_NAME)
            )
        )

    private fun findTeamOwner(teamOwnerId: TeamOwnerId): Either<NonEmptyList<Failure>, TeamOwner> =
        findTeamOwner.execute(teamOwnerId)
            .mapLeft { failure ->
                nonEmptyListOf(failure)
            }
}
