package tcla.contexts.tcla.webapi.springweb.assessment.checkwhetherassessmentcanbecreated

import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.assessment.checkwhetherassessmentcanbecreated.CheckWhetherAssessmentCanBeCreatedQuery
import tcla.contexts.tcla.core.application.assessment.checkwhetherassessmentcanbecreated.CheckWhetherAssessmentCanBeCreatedQueryHandler
import tcla.contexts.tcla.core.application.assessment.checkwhetherassessmentcanbecreated.CheckWhetherAssessmentCanBeCreatedSuccess
import tcla.contexts.tcla.webapi.springweb.assessment.checkwhetherassessmentcanbecreated.jsonapi.CheckWhetherAssessmentCanBeCreatedDocument
import tcla.contexts.tcla.webapi.springweb.assessment.checkwhetherassessmentcanbecreated.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse

@RestController
class CheckWhetherAssessmentCanBeCreatedController(
    private val checkWhetherAssessmentCanBeCreatedQueryHandler: CheckWhetherAssessmentCanBeCreatedQueryHandler
) {
    @GetMapping("/check-whether-assessment-can-be-created/{teamId}", produces = ["application/vnd.api+json"])
    fun execute(
        @PathVariable("teamId") teamId: String
    ): ResponseEntity<Any> =
        checkWhetherAssessmentCanBeCreatedQueryHandler.execute(CheckWhetherAssessmentCanBeCreatedQuery(teamId))
            .flatMap { querySuccess -> querySuccess.right() }
            .fold({ it.toFailureResponse() }, { it.toSuccessResponse() })


    private fun CheckWhetherAssessmentCanBeCreatedSuccess.toSuccessResponse(): ResponseEntity<Any> =
        CheckWhetherAssessmentCanBeCreatedDocument(data = this.toResource()).let { ResponseEntity.ok(it) }
}
