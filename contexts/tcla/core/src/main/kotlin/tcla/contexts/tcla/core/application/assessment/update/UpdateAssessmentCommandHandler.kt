package tcla.contexts.tcla.core.application.assessment.update

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.cancel.CancelAssessment
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.Title
import tcla.contexts.tcla.core.domain.assessment.rule.RequesterOwnsAssessmentRule
import tcla.contexts.tcla.core.domain.extractFirstName
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.teammembermessage.TeamMemberMessageRepository
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessageId
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireFilterKey
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.rule.NotOverlappingQuestionnairesByTeamRule
import tcla.contexts.tcla.core.domain.respondent.RespondentFilterKey
import tcla.contexts.tcla.core.domain.respondent.RespondentRepository
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teammember.TeamMemberFilterKey
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.contexts.tcla.core.domain.teammember.model.TeamMember
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.time.Instant
import java.util.Hashtable
import java.util.UUID

@Named
class UpdateAssessmentCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val requesterOwnsAssessmentRule: RequesterOwnsAssessmentRule,
    private val assessmentRepository: AssessmentRepository,
    private val questionnaireRepository: QuestionnaireRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val teamMemberMessageRepository: TeamMemberMessageRepository,
    private val notOverlappingQuestionnairesByTeamRule: NotOverlappingQuestionnairesByTeamRule,
//    @Inject("\${web-application.base-url}") private val webApplicationBaseUrl: String,
    private val cancelAssessment: CancelAssessment,
    private val respondentRepository: RespondentRepository
) {
    fun execute(command: UpdateAssessmentCommand): Either<NonEmptyList<Failure>, UpdateAssessmentSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            ensureRequestIsAuthenticated()
                .flatMap { requesterId ->
                    val assessmentFieldsToUpdate = command.fields
                    command.id.toUuid()
                        .mapLeft { _: StringNotConformsUuid -> nonEmptyListOf(Failure.StringIsNotUuid.AssessmentId) }
                        .flatMap { uuid -> ensureAssessmentExists(uuid) }
                        .flatMap { assessmentId -> findAssessment(assessmentId) }
                        .flatMap { assessment -> ensureRequesterOwnsAssessment(assessment, requesterId) }
                        .flatMap { assessment -> updateAssessment(assessment, assessmentFieldsToUpdate) }
                        .flatMap { updatedAssessment ->
                            findSurvey(updatedAssessment)
                                .flatMap { survey ->
                                    survey.updateResponseAcceptanceIntervalIfNeeded(assessmentFieldsToUpdate)
                                        .flatMap { updatedQuestionnaire ->
                                            ensureNotOverlapsWithAnotherSurvey(updatedQuestionnaire, updatedAssessment)
                                        }.flatMap { updatedQuestionnaire -> saveSurvey(updatedQuestionnaire) }
                                        .flatMap { updatedQuestionnaire ->
                                            scheduleSurveyDurationExtendedMessagesIfNeeded(
                                                updatedQuestionnaire,
                                                survey,
                                                updatedAssessment
                                            )
                                        }
                                }
                        }.flatMap { UpdateAssessmentSuccess.right() }
                }
        }

    private fun ensureRequesterOwnsAssessment(
        assessment: Assessment,
        requesterId: String
    ): Either<NonEmptyList<Failure>, Assessment> =
        requesterOwnsAssessmentRule.ensure(assessment, requesterId)
            .flatMap { assessment.right() }

    private fun ensureRequestIsAuthenticated(): Either<NonEmptyList<Failure>, String> =
        requestIsAuthenticatedRule.ensure()

    private fun findSurvey(updatedAssessment: Assessment): Either<NonEmptyList<Failure>, Questionnaire> =
        searchSurveyByAssessmentId(updatedAssessment.id)
            .flatMap { questionnaires: List<Questionnaire> ->
                Either.catch { questionnaires.first() }.mapLeft { Failure.DatabaseException(it).nel() }
            }

    private fun ensureNotOverlapsWithAnotherSurvey(
        updatedQuestionnaire: Questionnaire,
        updatedAssessment: Assessment
    ): Either<NonEmptyList<Failure>, Questionnaire> =
        notOverlappingQuestionnairesByTeamRule.ensure(
            updatedQuestionnaire.id,
            updatedQuestionnaire.responseAcceptanceInterval,
            updatedAssessment.teamId
        ).flatMap { updatedQuestionnaire.right() }

    private fun saveSurvey(updatedQuestionnaire: Questionnaire): Either<NonEmptyList<Failure>, Questionnaire> =
        questionnaireRepository.save(updatedQuestionnaire)

    private fun scheduleSurveyDurationExtendedMessagesIfNeeded(
        updatedQuestionnaire: Questionnaire,
        questionnaire: Questionnaire,
        updatedAssessment: Assessment
    ): Either<NonEmptyList<Failure>, Unit> {
        val surveyDurationExtended =
            updatedQuestionnaire.responseAcceptanceInterval.end > questionnaire.responseAcceptanceInterval.end
        return when (surveyDurationExtended) {
            true -> scheduleSurveyDurationExtendedMessages(updatedAssessment.teamId, updatedAssessment.id, updatedQuestionnaire.id)
            false -> Unit.right()
        }
    }

    private fun scheduleSurveyDurationExtendedMessages(
        teamId: TeamId,
        assessmentId: AssessmentId,
        questionnaireId: QuestionnaireId
    ): Either<NonEmptyList<Failure>, Unit> =
        teamMemberRepository.search(OneValueFilter(TeamMemberFilterKey.TEAM, teamId))
            .mapLeft { nonEmptyListOf(it) }
            .flatMap { teamMembers ->
                val filter = OneValueFilter(RespondentFilterKey.ASSESSMENT, assessmentId)
                respondentRepository.search(filter)
                    .flatMap { respondents ->
                        respondents.map { respondent ->
                            val teamMember: TeamMember =
                                teamMembers.find { teamMember: TeamMember -> teamMember.email.string == respondent.email.string }!!
                            createTeamMemberMessageForOneTeamMember(questionnaireId, teamMember, respondentName = respondent.name, respondentId = respondent.id)
                        }.right()
                    }
            }.flatMap {
                //TODO ensure uuids are unique
                it.right()
            }.flatMap {
                //TODO ensure messages not exists yet
                it.right()
            }
            .flatMap { teamMemberMessages ->
                teamMemberMessageRepository.saveAll(teamMemberMessages)
            }

    private fun findAssessment(assessmentId: AssessmentId): Either<NonEmptyList<Failure>, Assessment> =
        assessmentRepository.find(assessmentId)

    private fun updateAssessment(
        assessment: Assessment,
        assessmentFieldsToUpdate: HashMap<String, String?>
    ): Either<NonEmptyList<Failure>, Assessment> =
        assessment.update(assessmentFieldsToUpdate)
        .flatMap { updatedAssessment -> assessmentRepository.save(updatedAssessment) }

    private fun Assessment.update(
        assessmentFieldsToUpdate: HashMap<String, String?>
    ): Either<NonEmptyList<Failure>, Assessment> =
        updateTitle(assessmentFieldsToUpdate)
            .flatMap { updatedAssessment -> updatedAssessment.updateCurrentStep(assessmentFieldsToUpdate) }

    private fun Assessment.updateTitle(assessmentFieldsToUpdate: HashMap<String, String?>): Either<NonEmptyList<Failure>, Assessment> =
        when (assessmentFieldsToUpdate.containsKey("title")) {
            true -> when (val title = assessmentFieldsToUpdate.title()) {
                null -> TODO()
                else -> updateTitle(Title(title)).right()
            }

            false -> right()
        }

    private fun Assessment.updateCurrentStep(assessmentFieldsToUpdate: HashMap<String, String?>): Either<NonEmptyList<Failure>, Assessment> {
        return when (assessmentFieldsToUpdate.containsKey("currentStep")) {
            true -> when (val currentStep = assessmentFieldsToUpdate.currentStep()) {
                null -> TODO()
                else -> when (currentStep) {
                    "canceled" -> cancelAssessment.execute(this)
                    else -> TODO()
                }
            }

            false -> right()
        }
    }

    private fun searchSurveyByAssessmentId(assessmentId: AssessmentId): Either<NonEmptyList<Failure>, List<Questionnaire>> =
        questionnaireRepository.search(
            OneValueFilter(
                QuestionnaireFilterKey.ASSESSMENT,
                assessmentId
            )
        )

    private fun ensureAssessmentExists(uuid: UUID): Either<NonEmptyList<Failure>, AssessmentId> =
        assessmentRepository.exists(AssessmentId(uuid))
            .flatMap { assessmentExists ->
                when (assessmentExists) {
                    true -> AssessmentId(uuid).right()
                    false -> Failure.EntityNotFound.Assessment.nel().left()
                }
            }

    private fun Questionnaire.updateResponseAcceptanceIntervalIfNeeded(
        fields: HashMap<String, String?>
    ): Either<NonEmptyList<Failure>, Questionnaire> {
        val containsStartDate = fields.containsKey("responseAcceptanceIntervalStartDate")
        val startDateValue: String? = fields["responseAcceptanceIntervalStartDate"]
        val containsEndDate = fields.containsKey("responseAcceptanceIntervalEndDate")
        val endDateValue: String? = fields["responseAcceptanceIntervalEndDate"]

        return when {
            !containsStartDate && !containsEndDate -> Pair(
                responseAcceptanceInterval.start,
                responseAcceptanceInterval.end
            ).right()

            containsStartDate && containsEndDate -> {
                Either.catch { Instant.parse(startDateValue) }
                    .mapLeft { TODO() }
                    .flatMap { start: Instant ->
                        Either.catch { Instant.parse(endDateValue) }
                            .mapLeft { TODO() }
                            .flatMap { end: Instant -> Pair(start, end).right() }
                    }
            }

            !containsStartDate && containsEndDate -> {
                Either.catch { Instant.parse(endDateValue) }
                    .mapLeft { TODO() }
                    .flatMap { end: Instant -> Pair(responseAcceptanceInterval.start, end).right() }
            }

            else -> {
                Either.catch { Instant.parse(startDateValue) }
                    .mapLeft { TODO() }
                    .flatMap { start: Instant -> Pair(start, responseAcceptanceInterval.end).right() }
            }
        }.flatMap { startAndEnd ->
            responseAcceptanceInterval
                .update(newStart = startAndEnd.first, newEnd = startAndEnd.second)
                .mapLeft { responseAcceptanceIntervalFailure -> responseAcceptanceIntervalFailure.nel() }
                .flatMap { newInterval ->
                    updateResponseAcceptanceInterval(newInterval).right()
                }
        }
    }

    private fun createTeamMemberMessageForOneTeamMember(
        questionnaireId: QuestionnaireId,
        teamMember: TeamMember,
        respondentId: RespondentId,
        respondentName: String
    ): TeamMemberMessage = TeamMemberMessage(
        id = TeamMemberMessageId(UUID.randomUUID()),
        channel = Channel.EMAIL,
        type = TeamMemberMessage.Type.SURVEY_DURATION_EXTENDED,
        scheduledToBeSentAt = Instant.now(),
        actuallySentAt = null,
        teamMemberId = teamMember.id,
        respondentId = respondentId,
        surveyId = questionnaireId,
        extraData = buildSurveyDurationExtendedExtraData(
            teamMemberName = extractFirstName(respondentName),
            questionnaireId = questionnaireId,
//            webApplicationBaseUrl = webApplicationBaseUrl,
            respondentId = respondentId
        )
    )

    private fun buildSurveyDurationExtendedExtraData(
        teamMemberName: String,
//        webApplicationBaseUrl: String,
        questionnaireId: QuestionnaireId,
        respondentId: RespondentId
    ): Hashtable<String, String> = Hashtable(
        hashMapOf(
            Pair("teamMemberName", teamMemberName),
//            Pair("surveyUrl", "${webApplicationBaseUrl}/survey/${questionnaireId.uuid}?respondent=${respondentId.uuid}"),
            Pair("productName", PRODUCT_NAME)
        )
    )

    private fun HashMap<String, String?>.title(): String? = this["title"]
    private fun HashMap<String, String?>.currentStep(): String? = this["currentStep"]
}
