package tcla.contexts.tcla.core.application.failures



data object InvalidEmail : Failure() {
    override val humanReadableSummary: String = "Email is invalid"
}
