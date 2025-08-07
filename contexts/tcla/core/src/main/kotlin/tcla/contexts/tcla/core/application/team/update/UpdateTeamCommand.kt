package tcla.contexts.tcla.core.application.team.update

data class UpdateTeamCommand(
    val id: String,
    val fields: HashMap<String, Any?>
)
