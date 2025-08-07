package tcla.contexts.tcla.core.domain.questionnairefilling.create

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.assessment.AssessmentRepository
import tcla.contexts.tcla.core.domain.assessment.model.Assessment
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class CreateResponse(
    private val questionnaireRepository: QuestionnaireRepository,
    private val assessmentRepository: AssessmentRepository,
    private val transactionExecutor: TransactionExecutor
) {
    fun execute(
        questionnaireId: QuestionnaireId,
        questionnaireFilling: QuestionnaireFilling
    ): Either<Nothing, QuestionnaireFilling> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            questionnaireRepository.find(questionnaireId)
                .mapLeft { TODO() }
                .flatMap { questionnaire ->
                    assessmentRepository.find(questionnaire.assessmentId)
                        .mapLeft { TODO() }
                        .flatMap { assessment -> assessment.ensureIsInAppropriateStep() }
                        .flatMap {
                            //TODO ensure questionnaire filling id not yet exists in database
                            //TODO ensure all answers ids are unique and not yet exists in database
                            it.right()
                        }
                        .flatMap { assessment ->
                            questionnaire.addResponse(questionnaireFilling)
                                .mapLeft { TODO() }
                                .flatMap { updatedQuestionnaire ->
                                    when (updatedQuestionnaire.responses.currentAmountCollected) {
                                        updatedQuestionnaire.responses.maximumAmountToBeCollected.int -> assessment.moveToStep(Step.DataCollected).flatMap { updatedQuestionnaire.right() }
                                        else -> updatedQuestionnaire.right()
                                    }
                                }
                        }
                        .flatMap { updatedQuestionnaire ->
                            questionnaireRepository.save(updatedQuestionnaire)
                                .mapLeft { TODO() }
                        }.flatMap { questionnaireFilling.right() }
                }
        }

    private fun Assessment.moveToStep(step: Step): Either<Nothing, Assessment> =
        stepForwardTo(step)
            .mapLeft { TODO() }
            .flatMap { updatedAssessment ->
                assessmentRepository.save(updatedAssessment)
                    .mapLeft { TODO() }
            }

    private fun Assessment.ensureIsInAppropriateStep(): Either<Nothing, Assessment> =
        either {
            ensure(currentStep == Step.CollectingData) { TODO() }
            this@ensureIsInAppropriateStep
        }
}
