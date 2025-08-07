package tcla.contexts.tcla.webapi.springweb.assessment.create

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
import tcla.contexts.tcla.core.application.assessment.create.CreateAssessmentCommand
import tcla.contexts.tcla.core.application.assessment.create.CreateAssessmentCommandHandler
import tcla.contexts.tcla.core.application.assessment.create.CreateAssessmentSuccess
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.webapi.springweb.assessment.create.jsonapi.AssessmentPostRequestDocument
import tcla.contexts.tcla.webapi.springweb.assessment.jsonapi.ASSESSMENT_JSON_API_TYPE
import tcla.contexts.tcla.webapi.springweb.deserializeDocumentAs
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import java.net.URI

private val ALLOWED_JSON_API_TYPES = setOf(ASSESSMENT_JSON_API_TYPE)

@RestController
class CreateAssessmentController(
    private val createAssessmentCommandHandler: CreateAssessmentCommandHandler
) {

    @PostMapping("/assessments")
    fun execute(@RequestBody requestBody: String): ResponseEntity<Any> =
        requestBody.deserializeDocumentAs(AssessmentPostRequestDocument::class)
            .flatMap { document -> ensureAllowedJsonApiType(document) }
            .flatMap { document -> document.toCommand() }
            .flatMap { command -> createAssessmentCommandHandler.execute(command) }
            .fold(
                { failures: NonEmptyList<Failure> -> failures.toFailureResponse() },
                { commandSuccess -> commandSuccess.toSuccessResponse() }
            )

    private fun ensureAllowedJsonApiType(document: AssessmentPostRequestDocument): Either<NonEmptyList<Failure>, AssessmentPostRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { Failure.JsonApiTypeNotAllowed.nel() }
            document
        }

    private fun AssessmentPostRequestDocument.toCommand(): Either<NonEmptyList<Failure>, CreateAssessmentCommand> =
        with(data.attributes) {
            CreateAssessmentCommand(
                responseAcceptanceIntervalStartDate = responseAcceptanceIntervalStartDate,
                responseAcceptanceIntervalEndDate = responseAcceptanceIntervalEndDate,
                title = title,
                teamId = teamId,
                includeGenderQuestion = includeQuestionsOfInterest.gender,
                includeWorkFamiliarityQuestion = includeQuestionsOfInterest.workFamiliarity,
                includeTeamFamiliarityQuestion = includeQuestionsOfInterest.teamFamiliarity,
                includeModeOfWorkingQuestion = includeQuestionsOfInterest.modeOfWorking
            ).right()
        }

    private fun CreateAssessmentSuccess.toSuccessResponse(): ResponseEntity<Any> = ResponseEntity
        .created(URI.create("/${assessmentId.uuid}"))
        .contentType(MediaType("application", "vnd.api+json"))
        .build()
}
