package tcla.contexts.tcla.core.application.report.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.ResultsShareableToken
import tcla.contexts.tcla.core.domain.assessment.rule.RequesterOwnsAssessmentRule
import tcla.contexts.tcla.core.domain.organization.OrganizationFilterKey
import tcla.contexts.tcla.core.domain.organization.OrganizationRepository
import tcla.contexts.tcla.core.domain.organization.model.OwnerId
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.report.ReportFilterKey
import tcla.contexts.tcla.core.domain.report.buildreport.BuildReport
import tcla.contexts.tcla.core.domain.tcldriver.TclDriverRepository
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreFilterKey
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreRepository
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.StringNotConformsUuid
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class SearchReportsQueryHandler(
    private val transactionExecutor: TransactionExecutor,
    private val tclDriverScoreRepository: TclDriverScoreRepository,
    private val assessmentRepository: AssessmentRepository,
    private val questionnaireRepository: QuestionnaireRepository,
    private val tclDriverRepository: TclDriverRepository,
    private val buildReport: BuildReport,
    private val requesterOwnsAssessmentRule: RequesterOwnsAssessmentRule,
    private val teamRepository: TeamRepository,
    private val organizationRepository: OrganizationRepository
) {
    fun execute(query: SearchReportsQuery): Either<NonEmptyList<Failure>, SearchReportsSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            query.filters.ensureFilterIsSupported()
                .flatMap { filter -> filter.ensureAssessmentExists() }
                .flatMap { filter -> ensureRequesterOwnsAssessmentIfNeeded(filter) }
                .flatMap { filter: OneValueFilter<ReportFilterKey, out Any> ->
                    when (val value = filter.value) {
                        is AssessmentId -> OneValueFilter(TclDriverScoreFilterKey.ASSESSMENT, value).right()
                        is ResultsShareableToken -> OneValueFilter(TclDriverScoreFilterKey.RESULTS_SHAREABLE_TOKEN, value).right()
                        else -> Failure.FilterValuesNotAllowed.nel().left()
                    }
                }
                .flatMap { filter ->
                    tclDriverScoreRepository.search(filter)
                        .flatMap { tclDriverScores ->
                            tclDriverRepository.search()
                                .flatMap { tclDrivers ->
                                    buildTclDriverScorePairs(tclDriverScores, tclDrivers)
                                        .flatMap { tclDriverScorePairs ->
                                            when (val value = filter.value) {
                                                is AssessmentId -> assessmentRepository.find(value)
                                                is ResultsShareableToken -> assessmentRepository.findByResultsShareableToken(value)
                                                else -> Failure.FilterValuesNotAllowed.nel().left()
                                            }.flatMap { assessment ->
                                                when(assessment.questionnaireId) {
                                                    null -> Failure.DataWasExpectedToExist.Survey.nel().left()
                                                    else -> questionnaireRepository.find(assessment.questionnaireId)
                                                }.flatMap { survey: Questionnaire ->
                                                    teamRepository.find(assessment.teamId)
                                                        .flatMap { team ->
                                                            organizationRepository.search(OneValueFilter(OrganizationFilterKey.OWNER, OwnerId(team.ownerId.string)))
                                                                .flatMap { organizations ->
                                                                    buildReport.execute(
                                                                        tclDriverScorePairs = tclDriverScorePairs,
                                                                        resultsShareableToken = assessment.resultsShareableToken,
                                                                        responses = survey.responses,
                                                                        responseAcceptanceInterval = survey.responseAcceptanceInterval,
                                                                        organizationName = organizations.firstOrNull()?.name?.string,
                                                                        teamName = assessment.teamName,
                                                                        assessmentTitle = assessment.title.string
                                                                    ).right()
                                                                }
                                                        }
                                                }
                                            }
                                        }
                                }
                        }
                }
                .flatMap { report -> SearchReportsSuccess(listOf(report)).right() }
        }

    private fun ensureRequesterOwnsAssessmentIfNeeded(filter: OneValueFilter<ReportFilterKey, out Any>): Either<NonEmptyList<Failure>, OneValueFilter<ReportFilterKey, out Any>> =
        when (val value = filter.value) {
            is AssessmentId -> requesterOwnsAssessmentRule.ensure(value)
            is ResultsShareableToken -> Unit.right()
            else -> Failure.FilterValuesNotAllowed.nel().left()
        }.flatMap { filter.right() }

    private fun buildTclDriverScorePairs(
        tclDriverScores: List<TclDriverScore>,
        tclDrivers: List<TclDriver>
    ): Either<NonEmptyList<Failure>, List<Pair<TclDriver, TclDriverScore>>> =
        tclDriverScores.mapOrAccumulate { tclDriverScore ->
            Either.catch { tclDrivers.first { tclDriver -> tclDriver.id == tclDriverScore.tclDriverId } }
                .mapLeft { Failure.DataWasExpectedToExist.TclDriver }
                .flatMap { Pair(it, tclDriverScore).right() }.bind()
        }

    private fun OneValueFilter<ReportFilterKey, out Any>.ensureAssessmentExists(): Either<NonEmptyList<Failure>, OneValueFilter<ReportFilterKey, out Any>> =
        when(val value = value) {
            is AssessmentId -> assessmentRepository.exists(value)
            is ResultsShareableToken -> assessmentRepository.existsByResultsShareableToken(value)
            else -> Failure.FilterKeyNotAllowed.nel().left()
        }.flatMap { exists ->
            when (exists) {
                true -> right()
                false -> Failure.DataWasExpectedToExist.Assessment.nel().left()
            }
        }

    private fun List<ManyValuesFilter<String, String>>.ensureFilterIsSupported(): Either<NonEmptyList<Failure>, OneValueFilter<ReportFilterKey, out Any>> =
        when (this.isEmpty()) {
            true -> Failure.FilterIsNeeded.nel().left()
            false -> when(this.size > 1) {
                true -> Failure.FilterKeyNotAllowed.nel().left()
                false -> {
                    val filter: ManyValuesFilter<String, String> = this.first()
                    when(filter.key) {
                        "assessment" -> when (filter.values.isEmpty()) {
                            true -> Failure.NoValuesPresentInFilter.nel().left()
                            false -> when (filter.values.size > 1) {
                                true -> Failure.FilterValuesNotAllowed.nel().left()
                                false -> filter.values.first().toUuid()
                                    .mapLeft { _: StringNotConformsUuid -> Failure.StringIsNotUuid.AssessmentId.nel() }
                                    .flatMap { assessmentUuid: UUID ->
                                        OneValueFilter(
                                            ReportFilterKey.ASSESSMENT,
                                            AssessmentId(assessmentUuid)
                                        ).right()
                                    }
                            }
                        }
                        "shareable-token" -> when (filter.values.isEmpty()) {
                            true -> Failure.NoValuesPresentInFilter.nel().left()
                            false -> when (filter.values.size > 1) {
                                true -> Failure.FilterValuesNotAllowed.nel().left()
                                false -> Either.catch { filter.values.first() }
                                    .mapLeft { Failure.NoValuesPresentInFilter.nel() }
                                    .flatMap { token: String ->
                                        OneValueFilter(
                                            ReportFilterKey.SHAREABLE_TOKEN,
                                            ResultsShareableToken(token)
                                        ).right()
                                    }
                            }
                        }
                        else -> Failure.FilterKeyNotAllowed.nel().left()
                    }
                }
            }
        }
}
