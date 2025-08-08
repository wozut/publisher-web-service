package tcla.contexts.tcla.webapi.springweb.team.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.team.search.SearchTeamsQuery
import tcla.contexts.tcla.core.application.team.search.SearchTeamsQueryHandler
import tcla.contexts.tcla.core.application.team.search.SearchTeamsSuccess
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.team.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.team.search.jsonapi.TeamsDocument
import tcla.libraries.jsonapi.buildFilters


@RestController
class SearchTeamsController(
    private val searchTeamsQueryHandler: SearchTeamsQueryHandler
) {

    @GetMapping("/teams", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[owner]",
            required = false
        ) ownerFilterValue: String?,
        @RequestParam(
            name = "filter[organization]",
            required = false
        ) organizationFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchTeamsQuery(buildFilters(listOf(Pair("owner", ownerFilterValue), Pair("organization", organizationFilterValue)))))
            .flatMap { searchTeamsSuccess -> searchTeamsSuccess.teams.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })

    private fun search(query: SearchTeamsQuery): Either<NonEmptyList<Failure>, SearchTeamsSuccess> =
        searchTeamsQueryHandler.execute(query)

    private fun List<Team>.toSuccessResponse(): ResponseEntity<Any> =
        TeamsDocument(this.map { team -> team.toResource() })
            .let { ResponseEntity.ok(it) }
}
