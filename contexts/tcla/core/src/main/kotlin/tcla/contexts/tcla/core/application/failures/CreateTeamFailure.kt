package tcla.contexts.tcla.core.application.failures

sealed class CreateTeamFailure: Failure() {
    data object MaximumAmountOfTeamsPerOrganizationReached : CreateTeamFailure() {
        override val humanReadableSummary: String = "Maximum Amount of Teams per Organization Reached"
    }
}
