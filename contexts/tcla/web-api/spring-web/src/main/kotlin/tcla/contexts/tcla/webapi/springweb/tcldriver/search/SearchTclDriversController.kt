package tcla.contexts.tcla.webapi.springweb.tcldriver.search


import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.tcldriver.search.SearchTclDriversQuery
import tcla.contexts.tcla.core.application.tcldriver.search.SearchTclDriversQueryHandler
import tcla.contexts.tcla.core.application.tcldriver.search.SearchTclDriversSuccess
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.tcldriver.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.tcldriver.search.jsonapi.TclDriversDocument


@RestController
class SearchTclDriversController(private val searchTclDriversQueryHandler: SearchTclDriversQueryHandler) {

    @GetMapping("/tcl-drivers", produces = ["application/vnd.api+json"])
    fun execute(): ResponseEntity<Any> =
        search(SearchTclDriversQuery())
            .flatMap { searchTclDriversSuccess -> searchTclDriversSuccess.tclDrivers.right() }
            .fold(ifLeft = { it.toFailureResponse() }, ifRight = { it.toSuccessResponse() })

    private fun search(query: SearchTclDriversQuery): Either<NonEmptyList<Failure>, SearchTclDriversSuccess> =
        searchTclDriversQueryHandler.execute(query)

    private fun List<TclDriver>.toSuccessResponse(): ResponseEntity<Any> =
        TclDriversDocument(this.map { tclDriver -> tclDriver.toResource() })
            .let { tclDriversDocument: TclDriversDocument -> ResponseEntity.ok(tclDriversDocument) }
}


