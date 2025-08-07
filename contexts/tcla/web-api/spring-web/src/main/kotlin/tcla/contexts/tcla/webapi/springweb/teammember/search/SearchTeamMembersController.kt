package tcla.contexts.tcla.webapi.springweb.teammember.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.teammember.search.SearchTeamMembersQuery
import tcla.contexts.tcla.core.application.teammember.search.SearchTeamMembersQueryHandler
import tcla.contexts.tcla.core.application.teammember.search.SearchTeamMembersSuccess
import tcla.contexts.tcla.core.domain.teammember.model.TeamMember
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.teammember.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.teammember.search.jsonapi.TeamMembersDocument
import tcla.libraries.jsonapi.buildFilter


@RestController
class SearchTeamMembersController(
    private val searchTeamMembersQueryHandler: SearchTeamMembersQueryHandler
) {

    @GetMapping("/team-members", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[team]",
            required = false
        ) teamFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchTeamMembersQuery(buildFilter("team", teamFilterValue)))
            .flatMap { searchTeamMembersSuccess -> searchTeamMembersSuccess.teamMembers.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })

    private fun search(query: SearchTeamMembersQuery): Either<NonEmptyList<Failure>, SearchTeamMembersSuccess> =
        searchTeamMembersQueryHandler.execute(query)

    private fun List<TeamMember>.toSuccessResponse(): ResponseEntity<Any> =
        TeamMembersDocument(this.map { teamMember -> teamMember.toResource() })
            .let { ResponseEntity.ok(it) }

}
