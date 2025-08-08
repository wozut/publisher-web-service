package tcla.contexts.tcla.core.domain.questionnaire.model

sealed class QuestionnaireFailure {
    data class Responses(val failure: ResponsesFailure) : QuestionnaireFailure()
}
