package tcla.contexts.tcla.webapi.springweb.assessment.create.jsonapi


data class AssessmentPostResourceAttributes(
    val responseAcceptanceIntervalStartDate: String,
    val responseAcceptanceIntervalEndDate: String,
    val title: String,
    val teamId: String,
    val includeQuestionsOfInterest: IncludeQuestionsOfInterestAttribute
)
