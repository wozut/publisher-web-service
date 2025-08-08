package tcla.contexts.tcla.webapi.springweb.tcldriverscore.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.tcldriverscore.search.SearchTclDriverScoresQuery
import tcla.contexts.tcla.core.application.tcldriverscore.search.SearchTclDriverScoresQueryHandler
import tcla.contexts.tcla.core.application.tcldriverscore.search.SearchTclDriverScoresSuccess
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.tcldriverscore.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.tcldriverscore.search.jsonapi.TclDriverScoresDocument
import tcla.libraries.jsonapi.buildFilter


@RestController
class SearchTclDriverScoresController(
    private val searchTclDriverScoresQueryHandler: SearchTclDriverScoresQueryHandler
) {

    @GetMapping("/tcl-driver-scores", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[assessment]",
            required = false
        ) assessmentFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchTclDriverScoresQuery(buildFilter("assessment", assessmentFilterValue)))
            .flatMap { searchTclDriverScoresSuccess -> searchTclDriverScoresSuccess.tclDriverScores.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })

    private fun search(query: SearchTclDriverScoresQuery): Either<NonEmptyList<Failure>, SearchTclDriverScoresSuccess> =
        searchTclDriverScoresQueryHandler.execute(query)

    private fun List<TclDriverScore>.toSuccessResponse(): ResponseEntity<Any> =
        TclDriverScoresDocument(this.map { tclDriverScore -> tclDriverScore.toResource() })
            .let { ResponseEntity.ok(it) }
}
