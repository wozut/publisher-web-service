package tcla.contexts.tcla.core.application.teammember.create

sealed class CreateTeamMemberFailure {
    data object RequestNotAuthenticated : CreateTeamMemberFailure()
    data object InvalidName : CreateTeamMemberFailure()
    data object InvalidEmail : CreateTeamMemberFailure()
    data class DatabaseException(val exception: Throwable) : CreateTeamMemberFailure()
}
