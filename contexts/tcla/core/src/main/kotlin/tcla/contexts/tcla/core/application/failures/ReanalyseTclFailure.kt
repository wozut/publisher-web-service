package tcla.contexts.tcla.core.application.failures

sealed class ReanalyseTclFailure: Failure() {

    data object AssessmentHasNotAnAppropriateStatus : ReanalyseTclFailure() {
        override val humanReadableSummary: String = "Assessment has not an appropriate status"
    }

    data object DuplicatedAssessmentIds : ReanalyseTclFailure() {
        override val humanReadableSummary: String = "Duplicated assessment ids"
    }
}
