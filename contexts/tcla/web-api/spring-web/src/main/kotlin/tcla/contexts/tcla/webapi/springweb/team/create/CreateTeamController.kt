package tcla.contexts.tcla.webapi.springweb.team.create

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.team.create.CreateTeamCommand
import tcla.contexts.tcla.core.application.team.create.CreateTeamCommandHandler
import tcla.contexts.tcla.core.application.team.create.CreateTeamSuccess
import tcla.contexts.tcla.webapi.springweb.deserializeDocumentAs
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.team.create.jsonapi.TeamPostRequestDocument
import tcla.contexts.tcla.webapi.springweb.team.jsonapi.TEAM_JSON_API_TYPE
import java.net.URI

private val ALLOWED_JSON_API_TYPES = setOf(TEAM_JSON_API_TYPE)

@RestController
class CreateTeamController(
    private val createTeamCommandHandler: CreateTeamCommandHandler
) {

    @PostMapping("/teams")
    fun execute(@RequestBody requestBody: String): ResponseEntity<Any> =
        requestBody.deserializeDocumentAs(TeamPostRequestDocument::class)
            .flatMap { document -> ensureAllowedJsonApiType(document) }
            .flatMap { document -> document.toCommand() }
            .flatMap { command -> createTeamCommandHandler.execute(command) }
            .fold(
                { it.toFailureResponse() },
                { commandSuccess -> commandSuccess.toSuccessResponse() }
            )

    private fun ensureAllowedJsonApiType(document: TeamPostRequestDocument): Either<Nothing, TeamPostRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { TODO() }
            document
        }

    private fun TeamPostRequestDocument.toCommand(): Either<Nothing, CreateTeamCommand> =
        with(data.attributes) {
            CreateTeamCommand(
                name = name,
                timeZone = timeZone,
                organizationId = organizationId
            ).right()
        }

    private fun CreateTeamSuccess.toSuccessResponse(): ResponseEntity<Any> {
        val location = URI.create("/${this.teamId.uuid}")
        return ResponseEntity
            .created(location)
            .contentType(MediaType("application", "vnd.api+json"))
            .build()
    }
}
