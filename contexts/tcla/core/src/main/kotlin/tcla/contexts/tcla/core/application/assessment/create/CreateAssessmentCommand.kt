package tcla.contexts.tcla.core.application.assessment.create

data class CreateAssessmentCommand(
    val responseAcceptanceIntervalStartDate: String,
    val responseAcceptanceIntervalEndDate: String,
    val title: String,
    val teamId: String,
    val includeGenderQuestion: Boolean,
    val includeWorkFamiliarityQuestion: Boolean,
    val includeTeamFamiliarityQuestion: Boolean,
    val includeModeOfWorkingQuestion: Boolean
)
