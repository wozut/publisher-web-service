package tcla.contexts.tcla.core.application.failures

sealed class CreateAssessmentFailure: Failure() {
    data object InvalidStartDateFormat : CreateAssessmentFailure() {
        override val humanReadableSummary: String = ""
    }

    data object InvalidEndDateFormat : CreateAssessmentFailure() {
        override val humanReadableSummary: String = ""
    }

    data object StartDateMustBeNowOrAfter : CreateAssessmentFailure() {
        override val humanReadableSummary: String = ""
    }

    data object ResultsShareableTokenAlreadyExists : CreateAssessmentFailure() {
        override val humanReadableSummary: String = ""
    }

    data object UnableToCreate : CreateAssessmentFailure() {
        override val humanReadableSummary: String = "Unable to create"
    }

}
