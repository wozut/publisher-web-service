package tcla.contexts.tcla.core.application.action.create

data class CreateActionCommand(
    val assessmentId: String,
    val title: String,
    val targetQuestionsToThinkAbout: List<String>,
    val targetTclDrivers: List<String>,
    val description: String,
    val context: String? = null,
    val challenges: String? = null,
    val goals: String? = null
)
