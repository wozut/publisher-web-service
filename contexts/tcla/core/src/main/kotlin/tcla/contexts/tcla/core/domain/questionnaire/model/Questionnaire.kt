package tcla.contexts.tcla.core.domain.questionnaire.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling

data class Questionnaire(
    val id: QuestionnaireId,
    val externalQuestionnaireId: ExternalQuestionnaireId?,
    val responseAcceptanceInterval: ResponseAcceptanceInterval,
    val externalQuestionnaireIsPublic: Boolean,
    val questions: Set<Question>,
    val assessmentId: AssessmentId,
    val responses: Responses
) {
    fun hasStarted(): Boolean = responseAcceptanceInterval.hasStarted()
    fun hasEnded(): Boolean = responseAcceptanceInterval.hasEnded()
    fun updateResponseAcceptanceInterval(responseAcceptanceInterval: ResponseAcceptanceInterval): Questionnaire =
        copy(responseAcceptanceInterval = responseAcceptanceInterval)

    fun markExternalQuestionnaireAsPublic(): Questionnaire = copy(externalQuestionnaireIsPublic = true)

    fun linkToExternalQuestionnaire(externalQuestionnaireId: ExternalQuestionnaireId) =
        copy(externalQuestionnaireId = externalQuestionnaireId)

    fun addResponse(questionnaireFilling: QuestionnaireFilling): Either<QuestionnaireFailure, Questionnaire> =
        responses.add(questionnaireFilling)
            .mapLeft { QuestionnaireFailure.Responses(it) }
            .flatMap { updatedResponses -> copy(responses = updatedResponses).right() }
}
