package tcla.contexts.tcla.webapi.springweb.report.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.report.search.SearchReportsQuery
import tcla.contexts.tcla.core.application.report.search.SearchReportsQueryHandler
import tcla.contexts.tcla.core.application.report.search.SearchReportsSuccess
import tcla.contexts.tcla.core.domain.report.model.Report
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.report.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.report.search.jsonapi.ReportsDocument
import tcla.libraries.jsonapi.buildFilters


@RestController
class SearchReportsController(
    private val searchReportsQueryHandler: SearchReportsQueryHandler
) {

    @GetMapping("/reports", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[assessment]",
            required = false
        ) assessmentFilterValue: String?,
        @RequestParam(
            name = "filter[shareable-token]",
            required = false
        ) tokenFilterValue: String?
    ): ResponseEntity<Any> {
        val filters = buildFilters(listOf(
            Pair("assessment", assessmentFilterValue),
            Pair("shareable-token", tokenFilterValue)
        ))
        return search(SearchReportsQuery(filters))
            .flatMap { searchReportsSuccess -> searchReportsSuccess.reports.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })
    }


    private fun search(query: SearchReportsQuery): Either<NonEmptyList<Failure>, SearchReportsSuccess> =
        searchReportsQueryHandler.execute(query)

    private fun List<Report>.toSuccessResponse(): ResponseEntity<Any> =
        ReportsDocument(this.map { report -> report.toResource() })
            .let { reportsDocument -> ResponseEntity.ok(reportsDocument) }
}
