package tcla.contexts.analytics

sealed class Failure {
    /**
     * IMPORTANT: the value of this field on each concrete failure
     * MUST NOT CHANGE from occurrence to occurrence
     */
    abstract val humanReadableSummary: String

    data class DatabaseException(val exception: Throwable) : Failure() {
        override val humanReadableSummary: String = "Database exception"
    }

    sealed class EntityNotFound: Failure() {
        override val humanReadableSummary: String = "Entity not found"

        data object Onboarding : EntityNotFound()
    }

    data object RequestNotAuthenticated : Failure() {
        override val humanReadableSummary: String = "Authentication required"
    }

    data object InvalidId : Failure() {
        override val humanReadableSummary: String = "Id is not valid"
    }

    data object InvalidDocument : Failure() {
        override val humanReadableSummary: String = "Document is not valid"
    }

    data object JsonApiTypeNotAllowed : Failure() {
        override val humanReadableSummary: String = "JSON:API type not allowed"
    }
}
