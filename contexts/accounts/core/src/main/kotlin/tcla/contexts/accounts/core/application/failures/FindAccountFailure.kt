package tcla.contexts.accounts.core.application.failures

sealed class FindAccountFailure: Failure() {
    data object RequestNotAuthenticated : FindAccountFailure() {
        override val humanReadableSummary: String = "Authentication required"
    }

    data object NotFound : FindAccountFailure() {
        override val humanReadableSummary: String = "Account not found"
    }

    data object Unauthorized : FindAccountFailure() {
        override val humanReadableSummary: String = "Unauthorized"
    }

    data class TechnicalException(val exception: Throwable) : FindAccountFailure() {
        override val humanReadableSummary: String = "Technical exception"
    }

}
