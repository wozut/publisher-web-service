package tcla.contexts.tcla.webapi.springweb.action.create.jsonapi


data class ActionPostResourceAttributes(
    val assessmentId: String,
    val title: String,
    val targetQuestionsToThinkAbout: List<String>,
    val targetTclDrivers: List<String>,
    val description: String,
    val context: String?,
    val challenges: String?,
    val goals: String?
)
