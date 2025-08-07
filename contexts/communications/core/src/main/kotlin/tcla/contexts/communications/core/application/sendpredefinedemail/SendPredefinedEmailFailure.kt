package tcla.contexts.communications.core.application.sendpredefinedemail

sealed class SendPredefinedEmailFailure {
    data class RequestNotExecuted(val exception: Throwable): SendPredefinedEmailFailure()
    data class ErrorResponse(val code: Int, val body: String?) : SendPredefinedEmailFailure()
    data object UnexpectedResponse : SendPredefinedEmailFailure()
    data object InvalidUrl : SendPredefinedEmailFailure()
}
