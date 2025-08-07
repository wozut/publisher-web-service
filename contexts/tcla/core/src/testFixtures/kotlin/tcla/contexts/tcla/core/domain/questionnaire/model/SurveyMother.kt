package tcla.contexts.tcla.core.domain.questionnaire.model

import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.question.model.Question
import java.util.UUID

object SurveyMother {
    fun default(
        id: QuestionnaireId = QuestionnaireId(UUID.randomUUID()),
        externalQuestionnaireId: ExternalQuestionnaireId = ExternalQuestionnaireId(value = "GTRAg"),
        externalQuestionnaireIsPublic: Boolean = false,
        responseAcceptanceInterval: ResponseAcceptanceInterval = ResponseAcceptanceIntervalMother.default(),
        assessmentId: AssessmentId = AssessmentId(UUID.randomUUID()),
        questions: Set<Question> = emptySet(),
        responses: Responses = ResponsesMother.default()
    ): Questionnaire = Questionnaire(
        id = id,
        externalQuestionnaireId = externalQuestionnaireId,
        responseAcceptanceInterval = responseAcceptanceInterval,
        externalQuestionnaireIsPublic = externalQuestionnaireIsPublic,
        assessmentId = assessmentId,
        questions = questions,
        responses = responses
    )
}
