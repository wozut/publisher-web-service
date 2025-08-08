package tcla.contexts.tcla.webapi.springweb.teammember.delete

import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.teammember.delete.DeleteTeamMemberCommand
import tcla.contexts.tcla.core.application.teammember.delete.DeleteTeamMemberCommandHandler
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse

@RestController
class DeleteTeamMemberController(
    private val deleteTeamMemberCommandHandler: DeleteTeamMemberCommandHandler
) {

    @DeleteMapping("/team-members/{id}")
    fun execute(@PathVariable id: String): ResponseEntity<Any> =
        DeleteTeamMemberCommand(id).right()
            .flatMap { command -> deleteTeamMemberCommandHandler.execute(command) }
            .fold(
                { failures-> failures.toFailureResponse() },
                { toSuccessResponse() }
            )

    private fun toSuccessResponse(): ResponseEntity<Any> = ResponseEntity.noContent()
        .build()
}
