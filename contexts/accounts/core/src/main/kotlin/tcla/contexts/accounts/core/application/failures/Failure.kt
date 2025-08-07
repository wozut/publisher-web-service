package tcla.contexts.accounts.core.application.failures

sealed class Failure {
    /**
     * IMPORTANT: the value of this field on each concrete failure
     * MUST NOT CHANGE from occurrence to occurrence
     */
    abstract val humanReadableSummary: String

    data object RequestNotAuthenticated : Failure() {
        override val humanReadableSummary: String = "Authentication required"
    }
}
