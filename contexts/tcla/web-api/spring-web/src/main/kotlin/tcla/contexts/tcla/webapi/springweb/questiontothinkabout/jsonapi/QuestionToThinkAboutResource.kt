package tcla.contexts.tcla.webapi.springweb.questiontothinkabout.jsonapi

import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout

data class QuestionToThinkAboutResource(
    val id: String,
    val attributes: QuestionToThinkAboutResourceAttributes
) {
    val type: String = QUESTION_TO_THINK_ABOUT_JSON_API_TYPE
}


fun QuestionToThinkAbout.toResource() = QuestionToThinkAboutResource(
    id = this.id.value.toString(),
    attributes = QuestionToThinkAboutResourceAttributes(
        text = this.text.value
    )
)
