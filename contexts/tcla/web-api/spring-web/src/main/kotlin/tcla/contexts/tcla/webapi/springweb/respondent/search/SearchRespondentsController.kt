package tcla.contexts.tcla.webapi.springweb.respondent.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.respondent.search.SearchRespondentsQuery
import tcla.contexts.tcla.core.application.respondent.search.SearchRespondentsQueryHandler
import tcla.contexts.tcla.core.application.respondent.search.SearchRespondentsSuccess
import tcla.contexts.tcla.core.domain.respondent.model.Respondent
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.respondent.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.respondent.search.jsonapi.RespondentsDocument
import tcla.libraries.jsonapi.buildFilter


@RestController
class SearchRespondentsController(
    private val searchRespondentsQueryHandler: SearchRespondentsQueryHandler
) {

    @GetMapping("/respondents", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[assessment]",
            required = false
        ) assessmentFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchRespondentsQuery(buildFilter("assessment", assessmentFilterValue)))
            .flatMap { searchTclDriverScoresSuccess -> searchTclDriverScoresSuccess.respondents.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })

    private fun search(query: SearchRespondentsQuery): Either<NonEmptyList<Failure>, SearchRespondentsSuccess> =
        searchRespondentsQueryHandler.execute(query)

    private fun List<Respondent>.toSuccessResponse(): ResponseEntity<Any> =
        RespondentsDocument(this.map { tclDriverScore -> tclDriverScore.toResource() })
            .let { ResponseEntity.ok(it) }
}
