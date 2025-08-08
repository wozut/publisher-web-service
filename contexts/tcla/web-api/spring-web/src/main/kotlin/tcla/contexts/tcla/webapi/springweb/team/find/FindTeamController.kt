package tcla.contexts.tcla.webapi.springweb.team.find

import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.team.find.FindTeamQuery
import tcla.contexts.tcla.core.application.team.find.FindTeamQueryHandler
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.team.find.jsonapi.TeamDocument
import tcla.contexts.tcla.webapi.springweb.team.jsonapi.toResource

@RestController
class FindTeamController(
    private val findTeamQueryHandler: FindTeamQueryHandler
) {
    @GetMapping("/teams/{id}", produces = ["application/vnd.api+json"])
    fun execute(
        @PathVariable id: String
    ): ResponseEntity<Any> =
        findTeamQueryHandler.execute(FindTeamQuery(id))
            .flatMap { querySuccess -> querySuccess.team.right() }
            .fold({ it.toFailureResponse() }, { it.toSuccessResponse() })

    private fun Team.toSuccessResponse(): ResponseEntity<Any> =
        TeamDocument(data = this.toResource()).let { ResponseEntity.ok(it) }
}
