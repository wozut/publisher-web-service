package tcla.contexts.tcla.core.application.action.update

data class UpdateActionCommand(
    val id: String,
    val fields: HashMap<String, Any?>
)
