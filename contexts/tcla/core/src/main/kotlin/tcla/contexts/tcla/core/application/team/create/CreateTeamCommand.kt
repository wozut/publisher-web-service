package tcla.contexts.tcla.core.application.team.create

data class CreateTeamCommand(
    val name: String,
    val timeZone: String,
    val organizationId: String
)
