package tcla.contexts.tcla.core.domain.questionnaire.publishexternalquestionnaire

sealed class PublishExternalQuestionnaireFailure {
    data object InvalidUrl : PublishExternalQuestionnaireFailure()
    data class RequestNotExecuted(val exception: Throwable) : PublishExternalQuestionnaireFailure()
    data class ErrorResponse(val code: Int, val body: String?) : PublishExternalQuestionnaireFailure()
}
