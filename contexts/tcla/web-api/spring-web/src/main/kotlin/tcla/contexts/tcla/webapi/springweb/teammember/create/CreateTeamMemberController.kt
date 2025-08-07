package tcla.contexts.tcla.webapi.springweb.teammember.create

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.teammember.create.CreateTeamMemberCommand
import tcla.contexts.tcla.core.application.teammember.create.CreateTeamMemberCommandHandler
import tcla.contexts.tcla.core.application.teammember.create.CreateTeamMemberFailure
import tcla.contexts.tcla.core.application.teammember.create.CreateTeamMemberSuccess
import tcla.contexts.tcla.webapi.springweb.teammember.create.jsonapi.TeamMemberPostRequestDocument
import tcla.contexts.tcla.webapi.springweb.teammember.jsonapi.TEAM_MEMBER_JSON_API_TYPE
import tcla.libraries.jsonserialization.deserialization.deserializeJsonAs
import java.net.URI

private val ALLOWED_JSON_API_TYPES = setOf(TEAM_MEMBER_JSON_API_TYPE)

@RestController
class CreateTeamMemberController(
    private val createTeamMemberCommandHandler: CreateTeamMemberCommandHandler
) {

    @PostMapping("/team-members", produces = ["application/vnd.api+json"])
    fun execute(@RequestBody requestBody: String): ResponseEntity<Any> =
        requestBody.deserializeJsonAs(TeamMemberPostRequestDocument::class)
            .mapLeft { TODO() }
            .flatMap { document -> ensureAllowedJsonApiType(document) }
            .flatMap { document -> document.toCommand() }
            .flatMap { command ->
                createTeamMemberCommandHandler.execute(command)
                    .mapLeft {
                        when (it) {
                            is CreateTeamMemberFailure.DatabaseException -> TODO()
                            CreateTeamMemberFailure.InvalidEmail -> TODO()
                            CreateTeamMemberFailure.InvalidName -> TODO()
                            CreateTeamMemberFailure.RequestNotAuthenticated -> TODO()
                        }
                    }
            }.fold(
                { TODO() },
                { commandSuccess -> commandSuccess.toSuccessResponse() }
            )

    private fun ensureAllowedJsonApiType(document: TeamMemberPostRequestDocument): Either<Nothing, TeamMemberPostRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { TODO() }
            document
        }

    private fun TeamMemberPostRequestDocument.toCommand(): Either<Nothing, CreateTeamMemberCommand> =
        with(data.attributes) {
            CreateTeamMemberCommand(
                name = name,
                email = email,
                teamId = teamId
            ).right()
        }

    private fun CreateTeamMemberSuccess.toSuccessResponse(): ResponseEntity<Any> {
        val location = URI.create("/${this.teamMemberId.uuid}")
        return ResponseEntity.created(location).build()
    }
}
