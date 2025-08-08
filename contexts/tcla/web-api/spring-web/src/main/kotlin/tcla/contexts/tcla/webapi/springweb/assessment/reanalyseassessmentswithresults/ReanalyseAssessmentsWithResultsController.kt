package tcla.contexts.tcla.webapi.springweb.assessment.reanalyseassessmentswithresults

import arrow.core.NonEmptyList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.reanalyseassessmentswithresults.ReanalyseAssessmentsWithResultsCommand
import tcla.contexts.tcla.core.application.reanalyseassessmentswithresults.ReanalyseAssessmentsWithResultsCommandHandler
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse

@RestController
class ReanalyseAssessmentsWithResultsController(
    private val reanalyseAssessmentsWithResultsCommandHandler: ReanalyseAssessmentsWithResultsCommandHandler
) {

    //@PostMapping("/reanalyse-assessments-with-results")
    fun execute(): ResponseEntity<Any> =
        reanalyseAssessmentsWithResultsCommandHandler.execute(ReanalyseAssessmentsWithResultsCommand)
            .fold(
                { failures: NonEmptyList<Failure> -> failures.toFailureResponse() },
                { _ -> toSuccessResponse() }
            )

    private fun toSuccessResponse(): ResponseEntity<Any> = ResponseEntity.ok().build()
}