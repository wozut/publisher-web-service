package tcla.contexts.tcla.webapi.springweb.organization.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.organization.search.SearchOrganizationsQuery
import tcla.contexts.tcla.core.application.organization.search.SearchOrganizationsQueryHandler
import tcla.contexts.tcla.core.application.organization.search.SearchOrganizationsSuccess
import tcla.contexts.tcla.core.domain.organization.model.Organization
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.organization.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.organization.search.jsonapi.OrganizationsDocument
import tcla.libraries.jsonapi.buildFilter


@RestController
class SearchOrganizationsController(
    private val searchOrganizationsQueryHandler: SearchOrganizationsQueryHandler
) {

    @GetMapping("/organizations", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[owner]",
            required = false
        ) ownerFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchOrganizationsQuery(buildFilter("owner", ownerFilterValue)))
            .flatMap { searchOrganizationsSuccess -> searchOrganizationsSuccess.organizations.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })

    private fun search(query: SearchOrganizationsQuery): Either<NonEmptyList<Failure>, SearchOrganizationsSuccess> =
        searchOrganizationsQueryHandler.execute(query)

    private fun List<Organization>.toSuccessResponse(): ResponseEntity<Any> =
        OrganizationsDocument(this.map { organization -> organization.toResource() })
            .let { ResponseEntity.ok(it) }
}
