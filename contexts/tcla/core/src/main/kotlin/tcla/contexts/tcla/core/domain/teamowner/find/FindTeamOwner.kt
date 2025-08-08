package tcla.contexts.tcla.core.domain.teamowner.find

import arrow.core.Either
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.teamowner.TeamOwnerRepository
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId

@Named
class FindTeamOwner(
    private val teamOwnerRepository: TeamOwnerRepository
) {
    fun execute(id: TeamOwnerId) : Either<Failure, TeamOwner> = teamOwnerRepository.find(id)
}
