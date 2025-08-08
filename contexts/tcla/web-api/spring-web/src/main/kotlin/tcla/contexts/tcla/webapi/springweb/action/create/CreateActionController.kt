package tcla.contexts.tcla.webapi.springweb.action.create

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
import tcla.contexts.tcla.core.application.action.create.CreateActionCommand
import tcla.contexts.tcla.core.application.action.create.CreateActionCommandHandler
import tcla.contexts.tcla.core.application.action.create.CreateActionSuccess
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.webapi.springweb.action.create.jsonapi.ActionPostRequestDocument
import tcla.contexts.tcla.webapi.springweb.action.jsonapi.ACTION_JSON_API_TYPE
import tcla.contexts.tcla.webapi.springweb.deserializeDocumentAs
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import java.net.URI

private val ALLOWED_JSON_API_TYPES = setOf(ACTION_JSON_API_TYPE)

@RestController
class CreateActionController(
    private val createActionCommandHandler: CreateActionCommandHandler
) {

    @PostMapping("/actions")
    fun execute(@RequestBody requestBody: String): ResponseEntity<Any> =
        requestBody.deserializeDocumentAs(ActionPostRequestDocument::class)
            .flatMap { document -> ensureAllowedJsonApiType(document) }
            .flatMap { document -> document.toCommand() }
            .flatMap { command -> createActionCommandHandler.execute(command) }
            .fold(
                { failures: NonEmptyList<Failure> -> failures.toFailureResponse() },
                { commandSuccess -> commandSuccess.toSuccessResponse() }
            )

    private fun ensureAllowedJsonApiType(document: ActionPostRequestDocument): Either<NonEmptyList<Failure>, ActionPostRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { Failure.JsonApiTypeNotAllowed.nel() }
            document
        }

    private fun ActionPostRequestDocument.toCommand(): Either<NonEmptyList<Failure>, CreateActionCommand> =
        CreateActionCommand(
            assessmentId = data.attributes.assessmentId,
            title = data.attributes.title,
            targetQuestionsToThinkAbout = data.attributes.targetQuestionsToThinkAbout,
            targetTclDrivers = data.attributes.targetTclDrivers,
            description = data.attributes.description,
            context = data.attributes.context,
            challenges = data.attributes.challenges,
            goals = data.attributes.goals
        ).right()


    private fun CreateActionSuccess.toSuccessResponse(): ResponseEntity<Any> =
        ResponseEntity.created(URI.create("/${actionId.value}"))
            .contentType(MediaType("application", "vnd.api+json"))
            .build()
}
