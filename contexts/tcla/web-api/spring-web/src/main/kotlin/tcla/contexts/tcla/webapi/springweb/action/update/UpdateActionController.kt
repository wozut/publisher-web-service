package tcla.contexts.tcla.webapi.springweb.action.update

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.action.update.UpdateActionCommand
import tcla.contexts.tcla.core.application.action.update.UpdateActionCommandHandler
import tcla.contexts.tcla.core.application.action.update.UpdateActionSuccess
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.webapi.springweb.action.jsonapi.ACTION_JSON_API_TYPE
import tcla.contexts.tcla.webapi.springweb.action.update.jsonapi.ActionPatchRequestDocument
import tcla.contexts.tcla.webapi.springweb.deserializeDocumentAs
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse


@RestController
class UpdateActionController(
    private val updateActionCommandHandler: UpdateActionCommandHandler
) {

    private val allowedJsonApiTypes = setOf(ACTION_JSON_API_TYPE)

    @PatchMapping("/actions/{actionId}")
    fun execute(
        @PathVariable actionId: String,
        @RequestBody requestBody: String
    ): ResponseEntity<Any> = requestBody.deserializeDocumentAs(ActionPatchRequestDocument::class)
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


    private fun ensureAllowedJsonApiType(document: ActionPatchRequestDocument): Either<NonEmptyList<Failure>, ActionPatchRequestDocument> =
        either {
            ensure(allowedJsonApiTypes.contains(document.data.type)) { Failure.JsonApiTypeNotAllowed.nel() }
            document
        }

    private fun executeCommand(
        id: String,
        attributes: Map<String, Any?>
    ): Either<NonEmptyList<Failure>, UpdateActionSuccess> =
        UpdateActionCommand(
            id = id,
            fields = HashMap(attributes)
        ).let { command ->
            updateActionCommandHandler.execute(command)
        }

    private fun successResponse(): ResponseEntity<Any> = ResponseEntity.noContent().build()
}
