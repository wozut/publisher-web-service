package tcla.contexts.tcla.webapi.springweb.questionstothinkaboutmap.search

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.questionstothinkaboutmap.search.SearchQuestionsToThinkAboutMapsQueryHandler
import tcla.contexts.tcla.core.application.questionstothinkaboutmap.search.SearchQuestionsToThinkAboutMapsSuccess
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout
import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.webapi.springweb.questionstothinkaboutmap.jsonapi.QUESTIONS_TO_THINK_ABOUT_MAP_JSON_API_TYPE
import tcla.contexts.tcla.webapi.springweb.questionstothinkaboutmap.search.jsonapi.DriverQuestionToThinkAboutMapResource
import tcla.contexts.tcla.webapi.springweb.questionstothinkaboutmap.search.jsonapi.QuestionsToThinkAboutMapsDocument
import tcla.contexts.tcla.webapi.springweb.questiontothinkabout.jsonapi.toResource
import java.util.UUID

@RestController
class SearchQuestionsToThinkAboutMapsController(private val searchQuestionsToThinkAboutMapsQueryHandler: SearchQuestionsToThinkAboutMapsQueryHandler) {
    @GetMapping("/questions-to-think-about-maps", produces = ["application/vnd.api+json"])
    fun execute(): ResponseEntity<Any> = searchQuestionsToThinkAboutMapsQueryHandler
        .execute()
        .toDocument()
        .toSuccessResponse()
}

private fun QuestionsToThinkAboutMapsDocument.toSuccessResponse(): ResponseEntity<Any> =
    ResponseEntity.ok(this)

private fun SearchQuestionsToThinkAboutMapsSuccess.toDocument(): QuestionsToThinkAboutMapsDocument =
   QuestionsToThinkAboutMapsDocument(
       data = this.questionsToThinkAboutMaps.map {
           DriverQuestionToThinkAboutMapResource(
               id = UUID.randomUUID().toString(),
               type = QUESTIONS_TO_THINK_ABOUT_MAP_JSON_API_TYPE,
               attributes = it.value.mapKeys { toCamelCase(it) }
                   .mapValues { entry ->
                       entry.value.map {question ->
                           question.toResource()
                       }
               }
           )
       }
   )

private fun toCamelCase(it: Map.Entry<Driver, List<QuestionToThinkAbout>>) =
    it.key.id.split('-').mapIndexed { index, element ->
        when (index) {
            0 -> element
            else -> element.replaceFirstChar { it.uppercaseChar() }
        }
    }.joinToString("")
