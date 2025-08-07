package tcla.contexts.tcla.webapi.springweb.questionstothinkaboutmap.search.jsonapi

import tcla.contexts.tcla.webapi.springweb.questiontothinkabout.jsonapi.QuestionToThinkAboutResource

data class QuestionsToThinkAboutMapsDocument(val data: List<DriverQuestionToThinkAboutMapResource>)

data class DriverQuestionToThinkAboutMapResource(
    val id: String,
    val type: String,
    val attributes: Map<String, List<QuestionToThinkAboutResource>>
)
