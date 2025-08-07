package tcla.contexts.tcla.webapi.springweb.teamowner.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.teamowner.search.SearchTeamOwnersQuery
import tcla.contexts.tcla.core.application.teamowner.search.SearchTeamOwnersQueryHandler
import tcla.contexts.tcla.core.application.teamowner.search.SearchTeamOwnersSuccess
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.teamowner.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.teamowner.search.jsonapi.TeamOwnersDocument
import tcla.libraries.jsonapi.buildFilters


@RestController
class SearchTeamOwnersController(
    private val searchTeamOwnersQueryHandler: SearchTeamOwnersQueryHandler
) {

    @GetMapping("/team-owners", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[assessment]",
            required = false
        ) assessmentFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchTeamOwnersQuery(buildFilters(listOf(Pair("assessment", assessmentFilterValue)))))
            .flatMap { searchTeamOwnersSuccess -> searchTeamOwnersSuccess.teamOwners.right() }
            .fold(
                ifLeft = { failures -> failures.toFailureResponse() },
                ifRight = { teamOwners -> teamOwners.toSuccessResponse() }
            )

    private fun search(query: SearchTeamOwnersQuery): Either<NonEmptyList<Failure>, SearchTeamOwnersSuccess> =
        searchTeamOwnersQueryHandler.execute(query)

    private fun List<TeamOwner>.toSuccessResponse(): ResponseEntity<Any> =
        TeamOwnersDocument(this.map { teamMember -> teamMember.toResource() })
            .let { ResponseEntity.ok(it) }
}
