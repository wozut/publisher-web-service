package tcla.contexts.tcla.webapi.springweb.team.delete

import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.team.delete.DeleteTeamCommand
import tcla.contexts.tcla.core.application.team.delete.DeleteTeamCommandHandler
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse

@RestController
class DeleteTeamController(
    private val deleteTeamCommandHandler: DeleteTeamCommandHandler
) {

    @DeleteMapping("/teams/{id}")
    fun execute(@PathVariable id: String): ResponseEntity<Any> =
        DeleteTeamCommand(id).right()
            .flatMap { command -> deleteTeamCommandHandler.execute(command) }
            .fold(
                { failures: NonEmptyList<Failure> -> failures.toFailureResponse() },
                { toSuccessResponse() }
            )

    private fun toSuccessResponse(): ResponseEntity<Any> = ResponseEntity.noContent().build()
}
