package tcla.contexts.tcla.core.application.assessment.checkwhetherassessmentcanbecreated

import java.time.Instant

data class CheckWhetherAssessmentCanBeCreatedSuccess(
    val result: Boolean,
    val earliestAvailableDateAfterLatestAssessment: Instant
)
