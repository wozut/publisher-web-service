package tcla.contexts.tcla.core.domain.questionnairefilling.downloadquestionnairefillings

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.DownloadQuestionnaireFillingsFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefilling.create.CreateResponse
import tcla.contexts.tcla.core.domain.questionnairefilling.downloadquestionnairefillings.downloadresponses.DownloadResponses
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class DownloadQuestionnaireFillings(
    private val transactionExecutor: TransactionExecutor,
    private val questionnaireRepository: QuestionnaireRepository,
    private val downloadResponses: DownloadResponses,
    private val createResponse: CreateResponse,
    private val assessmentRepository: AssessmentRepository
) {
    fun execute(assessmentId: AssessmentId): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            findAssessment(assessmentId)
                .flatMap { assessment -> assessment.ensureIsInAppropriateStep() }
                .flatMap { assessment -> ensureQuestionnaireExists(assessment.questionnaireId) }
                .flatMap { questionnaireId ->
                    findQuestionnaire(questionnaireId)
                        .flatMap { questionnaire -> questionnaire.ensureExternalQuestionnaireIdIsNotNull() }
                        .flatMap { questionnaire -> questionnaireRepository.save(questionnaire) }
                        .flatMap { survey ->
                            downloadExternalResponses(survey)
                                .flatMap { downloadedResponses ->
                                    downloadedResponses
                                        .filterNot { downloadedResponse ->
                                            survey.responses.collection.any { it.externalId == downloadedResponse.externalId }
                                        }.right()
                                }.flatMap { responses ->
                                    responses.mapOrAccumulate { response ->
                                        createResponse.execute(survey.id, response).bind()
                                    }
                                }
                        }.flatMap { Unit.right() }
                }
        }

    private fun findQuestionnaire(questionnaireId: QuestionnaireId): Either<NonEmptyList<Failure>, Questionnaire> =
        questionnaireRepository.find(questionnaireId)

    private fun findAssessment(assessmentId: AssessmentId): Either<NonEmptyList<Failure>, Assessment> =
        assessmentRepository.find(assessmentId)

    private fun ensureQuestionnaireExists(
        questionnaireId: QuestionnaireId?
    ): Either<NonEmptyList<Failure>, QuestionnaireId> =
        when (questionnaireId) {
            null -> DownloadQuestionnaireFillingsFailure.QuestionnaireNotFound.nel().left()
            else -> questionnaireId.right()
        }

    private fun Assessment.ensureIsInAppropriateStep(): Either<NonEmptyList<Failure>, Assessment> =
        either {
            ensure(currentStep == Step.CollectingData) { DownloadQuestionnaireFillingsFailure.AssessmentIsNotInAppropriateStep.nel() }
            this@ensureIsInAppropriateStep
        }

    private fun Questionnaire.ensureExternalQuestionnaireIdIsNotNull(): Either<NonEmptyList<Failure>, Questionnaire> =
        either {
            ensureNotNull(this@ensureExternalQuestionnaireIdIsNotNull.externalQuestionnaireId) { DownloadQuestionnaireFillingsFailure.ExternalQuestionnaireIdIsNull.nel() }
            this@ensureExternalQuestionnaireIdIsNotNull
        }

    private fun downloadExternalResponses(questionnaire: Questionnaire): Either<NonEmptyList<Failure>, Set<QuestionnaireFilling>> =
        downloadResponses.execute(questionnaire.externalQuestionnaireId, questionnaire.questions)
            .mapLeft { failure -> failure.nel() }

}
