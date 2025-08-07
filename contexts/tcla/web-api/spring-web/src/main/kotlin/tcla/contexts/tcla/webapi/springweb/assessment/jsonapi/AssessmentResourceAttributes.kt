package tcla.contexts.tcla.webapi.springweb.assessment.jsonapi


data class AssessmentResourceAttributes(
    val questionnaireId: String?,
    val teamId: String,
    val title: String,
    val currentStep: String,
    val isCancelable: Boolean,
    val teamName: String,
    val teamTimeZone: String
)
