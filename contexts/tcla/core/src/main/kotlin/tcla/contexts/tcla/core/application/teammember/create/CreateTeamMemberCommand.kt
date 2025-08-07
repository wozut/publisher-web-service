package tcla.contexts.tcla.core.application.teammember.create

data class CreateTeamMemberCommand(
    val name: String,
    val email: String,
    val teamId: String
)
