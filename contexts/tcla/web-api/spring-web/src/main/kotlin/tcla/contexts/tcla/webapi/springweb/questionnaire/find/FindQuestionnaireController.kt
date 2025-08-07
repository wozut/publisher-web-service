package tcla.contexts.tcla.webapi.springweb.questionnaire.find

import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.questionnaire.find.FindQuestionnaireQuery
import tcla.contexts.tcla.core.application.questionnaire.find.FindQuestionnaireQueryHandler
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.questionnaire.find.jsonapi.QuestionnaireDocument
import tcla.contexts.tcla.webapi.springweb.questionnaire.jsonapi.toResource

@RestController
class FindQuestionnaireController(private val findQuestionnaireQueryHandler: FindQuestionnaireQueryHandler) {

    @GetMapping("/surveys/{id}", produces = ["application/vnd.api+json"])
    fun execute(@PathVariable id: String): ResponseEntity<Any> =
        findQuestionnaireQueryHandler.execute(FindQuestionnaireQuery(id))
            .flatMap { querySuccess -> querySuccess.questionnaire.right() }
            .fold(
                ifLeft = { it.toFailureResponse() },
                ifRight = { it.toSuccessResponse() }
            )

    private fun Questionnaire.toSuccessResponse(): ResponseEntity<Any> =
        QuestionnaireDocument(toResource()).let { ResponseEntity.ok(it) }
}
