package tcla.contexts.tcla.webapi.springweb.action.find

import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.action.find.FindActionQuery
import tcla.contexts.tcla.core.application.action.find.FindActionQueryHandler
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.webapi.springweb.action.find.jsonapi.ActionDocument
import tcla.contexts.tcla.webapi.springweb.action.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse

@RestController
class FindActionController(
    private val findActionQueryHandler: FindActionQueryHandler
) {
    @GetMapping("/actions/{id}", produces = ["application/vnd.api+json"])
    fun execute(
        @PathVariable id: String
    ): ResponseEntity<Any> = findActionQueryHandler.execute(FindActionQuery(id))
        .flatMap { querySuccess -> querySuccess.action.right() }
        .fold({ it.toFailureResponse() }, { it.toSuccessResponse() })

    private fun Action.toSuccessResponse(): ResponseEntity<Any> =
        ActionDocument(data = this.toResource()).let { ResponseEntity.ok(it) }
}
