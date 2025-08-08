package tcla.contexts.accounts.core.application.failures

sealed class GetNewAccessTokenFailure : Failure() {
    data object UnsuccessfulResponse : GetNewAccessTokenFailure() {
        override val humanReadableSummary: String = "Get New Access Token - UnsuccessfulResponse"
    }

    data object UnexpectedJsonStructure : GetNewAccessTokenFailure() {
        override val humanReadableSummary: String = "Get New Access Token - UnexpectedJsonStructure"
    }

    data object UnexpectedObjectStructure : GetNewAccessTokenFailure() {
        override val humanReadableSummary: String = "Get New Access Token - UnexpectedObjectStructure"
    }

    data class TechnicalException(val throwable: Throwable) : GetNewAccessTokenFailure() {
        override val humanReadableSummary: String = "Get New Access Token - TechnicalException"
    }
}
