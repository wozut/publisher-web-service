package tcla.contexts.tcla.core.application.assessment.create

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.CreateAssessmentFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.ResultsShareableToken
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.assessment.model.Title
import tcla.contexts.tcla.core.domain.assessment.model.generateResultsShareableToken
import tcla.contexts.tcla.core.domain.assessment.rule.AssessmentCanBeCreatedRule
import tcla.contexts.tcla.core.domain.questionnaire.create.CreateSurvey
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import tcla.contexts.tcla.core.domain.respondent.CreateRespondent
import tcla.contexts.tcla.core.domain.schedulemessagesforsurvey.ScheduleMessagesForSurvey
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.team.rule.RequesterOwnsTeamRule
import tcla.contexts.tcla.core.domain.teammember.TeamMemberFilterKey
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid
import java.time.Instant
import java.util.UUID

@Named
class CreateAssessmentCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val requesterOwnsTeamRule: RequesterOwnsTeamRule,
    private val assessmentRepository: AssessmentRepository,
    private val createSurvey: CreateSurvey,
    private val scheduleMessagesForSurvey: ScheduleMessagesForSurvey,
    private val assessmentCanBeCreatedRule: AssessmentCanBeCreatedRule,
    private val teamMemberRepository: TeamMemberRepository,
    private val createRespondent: CreateRespondent
) {
    fun execute(command: CreateAssessmentCommand): Either<NonEmptyList<Failure>, CreateAssessmentSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { command.teamId.toDomainTeamId() }
                .flatMap { teamId -> requesterOwnsTeamRule.ensure(teamId) }
                .flatMap { team ->
                    ensureAssessmentCanBeCreated(team.id)
                        .flatMap { ensureAssessmentWithGivenIdDoesNotExist(AssessmentId(UUID.randomUUID())) }
                        .flatMap { assessmentId ->
                            command.teamId.toTeamUuid()
                                .flatMap { teamUuid ->
                                    generateNewResultsShareableToken()
                                        .flatMap { resultsShareableToken ->
                                            Assessment(
                                                id = assessmentId,
                                                title = Title(command.title),
                                                questionnaireId = null,
                                                currentStep = Step.Scheduled,
                                                teamId = TeamId(teamUuid),
                                                startedCollectingDataAt = null,
                                                resultsShareableToken = resultsShareableToken,
                                                teamName = team.name.string,
                                                teamTimeZone = team.timeZone
                                            ).right()
                                        }
                                }
                        }.flatMap { assessment -> assessmentRepository.save(assessment) }
                        .flatMap { assessment -> createRespondents(assessment.id, teamId = team.id).flatMap { assessment.right() } }
                }
                .flatMap { assessment ->
                    buildResponseAcceptanceInterval(
                        command.responseAcceptanceIntervalStartDate,
                        command.responseAcceptanceIntervalEndDate
                    ).mapLeft { nonEmptyListOf(it) }
                        .flatMap { interval ->
                            command.teamId.toDomainTeamId()
                                .flatMap { teamId ->
                                    createSurvey.execute(
                                        assessmentId = assessment.id,
                                        interval = interval,
                                        teamId = teamId,
                                        includeGenderQuestion = command.includeGenderQuestion,
                                        includeWorkFamiliarityQuestion = command.includeWorkFamiliarityQuestion,
                                        includeTeamFamiliarityQuestion = command.includeTeamFamiliarityQuestion,
                                        includeModeOfWorkingQuestion = command.includeModeOfWorkingQuestion
                                    )
                                }
                        }.flatMap { survey -> scheduleMessagesForSurvey.execute(assessment.teamId, survey) }
                        .flatMap { CreateAssessmentSuccess(assessment.id).right() }
                }
        }

    private fun createRespondents(assessmentId: AssessmentId, teamId: TeamId): Either<NonEmptyList<Failure>, Unit> =
        teamMemberRepository.search(OneValueFilter(TeamMemberFilterKey.TEAM, teamId))
            .mapLeft { failure -> failure.nel() }
            .flatMap { teamMembers ->
                teamMembers.mapOrAccumulate { teamMember ->
                    createRespondent.execute(teamMember.name.string, teamMember.email, assessmentId).bindNel()
                }
            }.flatMap { Unit.right() }

    private fun generateNewResultsShareableToken(): Either<NonEmptyList<Failure>, ResultsShareableToken> {
        val token = generateResultsShareableToken()
        return assessmentRepository.existsByResultsShareableToken(token)
            .flatMap { exists ->
                when (exists) {
                    true -> CreateAssessmentFailure.ResultsShareableTokenAlreadyExists.nel().left()
                    false -> token.right()
                }
            }
    }

    private fun ensureAssessmentCanBeCreated(teamId: TeamId): Either<NonEmptyList<Failure>, TeamId> =
        assessmentCanBeCreatedRule.ensure(teamId)
            .flatMap { teamId.right() }

    private fun String.toTeamUuid(): Either<NonEmptyList<Failure>, UUID> =
        toUuid()
            .mapLeft { nonEmptyListOf(Failure.StringIsNotUuid.TeamId) }

    private fun String.toDomainTeamId(): Either<NonEmptyList<Failure>, TeamId> =
        toTeamUuid()
            .flatMap { TeamId(it).right() }

    private fun buildResponseAcceptanceInterval(
        surveyStartDate: String,
        surveyEndDate: String
    ): Either<Failure, ResponseAcceptanceInterval> =
        Either.catch { Instant.parse(surveyStartDate) }
            .mapLeft { CreateAssessmentFailure.InvalidStartDateFormat }
            .flatMap { startInstant ->
                Either.catch { Instant.parse(surveyEndDate) }
                    .mapLeft { CreateAssessmentFailure.InvalidEndDateFormat }
                    .flatMap { endInstant ->
                        ensureStartDateIsNowOrAfter(startInstant)
                            .flatMap {
                                ResponseAcceptanceInterval(
                                    start = startInstant,
                                    end = endInstant
                                )
                            }
                    }
            }

    private fun ensureStartDateIsNowOrAfter(desiredStart: Instant): Either<CreateAssessmentFailure, Unit> =
        either {
            ensure(desiredStart >= Instant.now()) { CreateAssessmentFailure.StartDateMustBeNowOrAfter }
        }

    private fun ensureAssessmentWithGivenIdDoesNotExist(id: AssessmentId): Either<NonEmptyList<Failure>, AssessmentId> =
        assessmentRepository.exists(id)
            .flatMap { exists ->
                when (exists) {
                    true -> nonEmptyListOf(Failure.EntityAlreadyExists.Assessment).left()
                    false -> id.right()
                }
            }
}
