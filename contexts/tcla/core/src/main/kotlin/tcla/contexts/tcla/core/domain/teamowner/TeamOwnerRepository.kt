package tcla.contexts.tcla.core.domain.teamowner

import arrow.core.Either
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.search.Filter

interface TeamOwnerRepository {

    fun find(id: TeamOwnerId): Either<Failure, TeamOwner>
    fun search(filter: Filter<TeamOwnerFilterKey>): Either<Failure, List<TeamOwner>>
}
