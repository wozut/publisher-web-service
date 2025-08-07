package tcla.contexts.tcla.webapi.springweb.assessment.checkwhetherassessmentcanbecreated.jsonapi


data class CheckWhetherAssessmentCanBeCreatedResourceAttributes(
    val result: Boolean,
    val earliestAvailableDateAfterLatestAssessment: String
)
