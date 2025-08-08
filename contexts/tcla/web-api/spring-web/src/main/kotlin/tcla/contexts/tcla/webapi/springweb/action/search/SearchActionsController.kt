package tcla.contexts.tcla.webapi.springweb.action.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.action.search.SearchActionsQuery
import tcla.contexts.tcla.core.application.action.search.SearchActionsQueryHandler
import tcla.contexts.tcla.core.application.action.search.SearchActionsSuccess
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.webapi.springweb.action.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.action.search.jsonapi.ActionsDocument
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.libraries.jsonapi.buildFilter


@RestController
class SearchActionsController(
    private val searchActionsQueryHandler: SearchActionsQueryHandler
) {

    @GetMapping("/actions", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[assessment]",
            required = false
        ) assessmentFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchActionsQuery(buildFilter("assessment", assessmentFilterValue)))
            .flatMap { searchActionsSuccess -> searchActionsSuccess.actions.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })

    private fun search(query: SearchActionsQuery): Either<NonEmptyList<Failure>, SearchActionsSuccess> =
        searchActionsQueryHandler.execute(query)

    private fun List<Action>.toSuccessResponse(): ResponseEntity<Any> =
        ActionsDocument(this.map { action -> action.toResource() })
            .let { ResponseEntity.ok(it) }
}
