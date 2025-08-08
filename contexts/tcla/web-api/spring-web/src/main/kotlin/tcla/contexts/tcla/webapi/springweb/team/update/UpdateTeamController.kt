package tcla.contexts.tcla.webapi.springweb.team.update

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.team.update.UpdateTeamCommand
import tcla.contexts.tcla.core.application.team.update.UpdateTeamCommandHandler
import tcla.contexts.tcla.core.application.team.update.UpdateTeamSuccess
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.team.jsonapi.TEAM_JSON_API_TYPE
import tcla.contexts.tcla.webapi.springweb.team.update.jsonapi.TeamPatchRequestDocument
import tcla.libraries.jsonserialization.deserialization.deserializeJsonAs

private val ALLOWED_JSON_API_TYPES = setOf(TEAM_JSON_API_TYPE)

@RestController
class UpdateTeamController(
    private val updateTeamCommandHandler: UpdateTeamCommandHandler
) {

    @PatchMapping("/teams/{teamId}")
    fun execute(
        @PathVariable teamId: String,
        @RequestBody requestBody: String
    ): ResponseEntity<Any> = requestBody.deserializeJsonAs(TeamPatchRequestDocument::class)
        .mapLeft { TODO() }
        .flatMap {
            //TODO ensure path variable is equal to id in body
            it.right()
        }.flatMap { document -> ensureAllowedJsonApiType(document) }
        .flatMap { document ->
            when (document.data.attributes) {
                null -> mapOf()
                else -> document.data.attributes
            }.let { map: Map<String, Any?> ->
                executeCommand(document.data.id, map)
            }
        }.fold({ it.toFailureResponse() }, { successResponse() })


    private fun ensureAllowedJsonApiType(document: TeamPatchRequestDocument): Either<Nothing, TeamPatchRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { TODO() }
            document
        }

    private fun executeCommand(
        id: String,
        attributes: Map<String, Any?>
    ): Either<NonEmptyList<Failure>, UpdateTeamSuccess> =
        UpdateTeamCommand(
            id = id,
            fields = HashMap(attributes)
        ).let { command ->
            updateTeamCommandHandler.execute(command)
        }

    private fun successResponse(): ResponseEntity<Any> = ResponseEntity.noContent().build()
}
