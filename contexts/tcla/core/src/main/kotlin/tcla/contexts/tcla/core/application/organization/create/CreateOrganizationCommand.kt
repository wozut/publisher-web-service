package tcla.contexts.tcla.core.application.organization.create

data class CreateOrganizationCommand(
    val name: String,
    val industry: String,
    val size: String
)
