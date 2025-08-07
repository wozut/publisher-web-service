package tcla.contexts.tcla.core.domain.analysetcl

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.AnalyseTclFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.QuestionnaireFillingForAnalysisFilterKey
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.QuestionnaireFillingForAnalysisRepository
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysis
import tcla.contexts.tcla.core.domain.runtclmodel.RunTclModel
import tcla.contexts.tcla.core.domain.runtclmodel.RunTclModelFailure
import tcla.contexts.tcla.core.domain.runtclmodel.RunTclModelSuccess
import tcla.contexts.tcla.core.domain.scheduleresultsavailablemessage.ScheduleResultsAvailableMessage
import tcla.contexts.tcla.core.domain.tcldriverscore.SaveAllTclDriverScoresSuccess
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreRepository
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor


@Named
class AnalyseTcl(
    private val assessmentRepository: AssessmentRepository,
    private val questionnaireRepository: QuestionnaireRepository,
    private val questionnaireFillingForAnalysisRepository: QuestionnaireFillingForAnalysisRepository,
    private val runTclModel: RunTclModel,
    private val tclDriverScoreRepository: TclDriverScoreRepository,
    private val transactionExecutor: TransactionExecutor,
    private val scheduleResultsAvailableMessage: ScheduleResultsAvailableMessage
) {

    fun execute(assessmentId: AssessmentId): Either<NonEmptyList<Failure>, AnalyseTclSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            assessmentRepository.find(assessmentId)
                .flatMap { assessment -> assessment.ensureIsInAppropriateStep() }
                .flatMap { assessment ->
                    findQuestionnaire(assessment.questionnaireId)
                        .flatMap { questionnaire ->
                            searchQuestionnaireFillings(questionnaire)
                                .flatMap { questionnaireFillings -> questionnaireFillings.ensureIsNotEmpty() }
                                .flatMap { questionnaireFillings -> executeModel(questionnaireFillings, assessmentId) }
                                .flatMap { runTclModelSuccess: RunTclModelSuccess -> runTclModelSuccess.tclDriverScores.right() }
                                .flatMap { tclDriverScores: List<TclDriverScore> -> tclDriverScores.ensureNoneExist() }
                                .flatMap { tclDriverScores -> saveTclDriverScores(tclDriverScores) }
                                .flatMap {
                                    scheduleResultsAvailableMessage.execute(
                                        assessment.id,
                                        assessment.teamId,
                                        questionnaire.id
                                    )
                                }.flatMap { assessment.right() }
                        }
                }.flatMap { assessment: Assessment ->
                    assessment.stepForwardTo(Step.ResultsAvailable).mapLeft { it.nel() }
                }.flatMap { assessment ->
                    assessmentRepository.save(assessment)
                }.flatMap { AnalyseTclSuccess.right() }
        }

    private fun Assessment.ensureIsInAppropriateStep(): Either<NonEmptyList<Failure>, Assessment> =
        either {
            ensure(currentStep == Step.AnalysableData) { nonEmptyListOf(AnalyseTclFailure.AssessmentIsNotInAppropriateStep) }
            this@ensureIsInAppropriateStep
        }

    private fun List<QuestionnaireFillingForAnalysis>.ensureIsNotEmpty(): Either<NonEmptyList<Failure>, List<QuestionnaireFillingForAnalysis>> =
        either {
            ensure(this@ensureIsNotEmpty.isNotEmpty()) { nonEmptyListOf(AnalyseTclFailure.ThereAreNoResponses) }
            this@ensureIsNotEmpty
        }

    private fun saveTclDriverScores(tclDriverScores: List<TclDriverScore>): Either<NonEmptyList<Failure>, SaveAllTclDriverScoresSuccess> =
        tclDriverScoreRepository.saveAll(tclDriverScores)

    private fun List<TclDriverScore>.ensureNoneExist(): Either<NonEmptyList<Failure>, List<TclDriverScore>> =
        map { tclDriverScore -> tclDriverScore.id }
            .let { ids -> tclDriverScoreRepository.noneExists(ids) }
            .mapLeft { failure -> nonEmptyListOf(failure) }
            .flatMap { noneExists ->
                when (noneExists) {
                    true -> right()
                    false -> nonEmptyListOf(AnalyseTclFailure.TclDriverScoreAlreadyExist).left()
                }
            }

    private fun executeModel(
        questionnaireFillings: List<QuestionnaireFillingForAnalysis>,
        assessmentId: AssessmentId
    ): Either<NonEmptyList<Failure>, RunTclModelSuccess> =
        runTclModel.execute(questionnaireFillings, assessmentId)
            .mapLeft { failure: RunTclModelFailure ->
                nonEmptyListOf(
                    when (failure) {
                        RunTclModelFailure.InsufficientCells -> AnalyseTclFailure.InsufficientCells
                        RunTclModelFailure.InsufficientRows -> AnalyseTclFailure.InsufficientRows
                        RunTclModelFailure.InvalidURL -> AnalyseTclFailure.InvalidURL
                        is RunTclModelFailure.TclModelCallException -> AnalyseTclFailure.TclModelCallException
                        is RunTclModelFailure.TclModelRun -> AnalyseTclFailure.TclModelRun
                        RunTclModelFailure.UnableToCreateWorkbook -> AnalyseTclFailure.UnableToCreateWorkbook
                        RunTclModelFailure.UnableToGetSheet -> AnalyseTclFailure.UnableToGetSheet
                        RunTclModelFailure.UnsupportedData -> AnalyseTclFailure.UnsupportedData
                    }
                )
            }

    private fun findQuestionnaire(questionnaireId: QuestionnaireId?): Either<NonEmptyList<Failure>, Questionnaire> =
        when (questionnaireId) {
            null -> nonEmptyListOf(AnalyseTclFailure.MissingQuestionnaire).left()
            else -> questionnaireRepository.find(questionnaireId)
        }

    private fun searchQuestionnaireFillings(questionnaire: Questionnaire): Either<NonEmptyList<Failure>, List<QuestionnaireFillingForAnalysis>> =
        questionnaireFillingForAnalysisRepository.search(
            OneValueFilter(
                QuestionnaireFillingForAnalysisFilterKey.QUESTIONNAIRE, questionnaire.id
            )
        )
}


