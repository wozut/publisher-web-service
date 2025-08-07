package tcla.contexts.tcla.webapi.springweb.organization.create

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.organization.create.CreateOrganizationCommand
import tcla.contexts.tcla.core.application.organization.create.CreateOrganizationCommandHandler
import tcla.contexts.tcla.core.application.organization.create.CreateOrganizationSuccess
import tcla.contexts.tcla.webapi.springweb.deserializeDocumentAs
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.organization.create.jsonapi.OrganizationPostRequestDocument
import tcla.contexts.tcla.webapi.springweb.organization.jsonapi.ORGANIZATION_JSON_API_TYPE
import java.net.URI

private val ALLOWED_JSON_API_TYPES = setOf(ORGANIZATION_JSON_API_TYPE)
@RestController
class CreateOrganizationController(
    private val createOrganizationCommandHandler: CreateOrganizationCommandHandler
) {

    @PostMapping("/organizations")
    fun execute(@RequestBody requestBody: String): ResponseEntity<Any> =
        requestBody.deserializeDocumentAs(OrganizationPostRequestDocument::class)
            .flatMap { document -> ensureAllowedJsonApiType(document) }
            .flatMap { document -> document.toCommand() }
            .flatMap { command -> createOrganizationCommandHandler.execute(command) }
            .fold(
                { it.toFailureResponse() },
                { commandSuccess -> commandSuccess.toSuccessResponse() }
            )

    private fun ensureAllowedJsonApiType(document: OrganizationPostRequestDocument): Either<NonEmptyList<Failure>, OrganizationPostRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { Failure.JsonApiTypeNotAllowed.nel() }
            document
        }

    private fun OrganizationPostRequestDocument.toCommand(): Either<NonEmptyList<Failure>, CreateOrganizationCommand> =
        with(data.attributes) {
            CreateOrganizationCommand(
                name = name,
                industry = industry,
                size = size
            ).right()
        }

    private fun CreateOrganizationSuccess.toSuccessResponse(): ResponseEntity<Any> {
        val location = URI.create("/${this.organizationId.uuid}")
        return ResponseEntity
            .created(location)
            .contentType(MediaType("application", "vnd.api+json"))
            .build()
    }
}
