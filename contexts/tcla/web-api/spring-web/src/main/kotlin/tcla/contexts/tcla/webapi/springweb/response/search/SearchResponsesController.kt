package tcla.contexts.tcla.webapi.springweb.response.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.response.search.SearchResponsesFailure
import tcla.contexts.tcla.core.application.response.search.SearchResponsesQuery
import tcla.contexts.tcla.core.application.response.search.SearchResponsesQueryHandler
import tcla.contexts.tcla.core.application.response.search.SearchResponsesSuccess
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.contexts.tcla.webapi.springweb.response.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.response.search.jsonapi.ResponsesDocument
import tcla.libraries.jsonapi.buildFilters


@RestController
class SearchResponsesController(
    private val searchResponsesQueryHandler: SearchResponsesQueryHandler
) {

    @GetMapping("/responses", produces = ["application/vnd.api+json"])
    fun execute(
        @RequestParam(
            name = "filter[survey]",
            required = false
        ) surveyFilterValue: String?
    ): ResponseEntity<Any> =
        search(SearchResponsesQuery(buildFilters(listOf(Pair("survey", surveyFilterValue)))))
            .flatMap { success -> success.responses.right() }
            .fold(
                ifLeft = { failures -> failures.toFailureResponse() },
                ifRight = { teamOwners -> teamOwners.toSuccessResponse() }
            )

    private fun search(query: SearchResponsesQuery): Either<NonEmptyList<SearchResponsesFailure>, SearchResponsesSuccess> =
        searchResponsesQueryHandler.execute(query)

    private fun List<QuestionnaireFilling>.toSuccessResponse(): ResponseEntity<Any> =
        ResponsesDocument(this.map { response -> response.toResource() })
            .let { ResponseEntity.ok(it) }

    private fun NonEmptyList<SearchResponsesFailure>.toFailureResponse(): ResponseEntity<Any> = TODO()
}
