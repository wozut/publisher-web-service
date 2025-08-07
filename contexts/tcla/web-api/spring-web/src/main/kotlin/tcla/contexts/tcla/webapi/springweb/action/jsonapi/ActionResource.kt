package tcla.contexts.tcla.webapi.springweb.action.jsonapi

import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.webapi.springweb.questiontothinkabout.jsonapi.QuestionToThinkAboutResource
import tcla.contexts.tcla.webapi.springweb.questiontothinkabout.jsonapi.toResource
import tcla.contexts.tcla.webapi.springweb.tcldriver.jsonapi.TclDriverResource
import tcla.contexts.tcla.webapi.springweb.tcldriver.jsonapi.toResource


data class ActionResource(
    val id: String,
    val attributes: ActionResourceAttributes,
) {
    val type: String = ACTION_JSON_API_TYPE
}

data class ActionResourceAttributes(
    val assessmentId: String,
    val title: String,
    val targetQuestionsToThinkAbout: List<QuestionToThinkAboutResource>,
    val targetTclDrivers: List<TclDriverResource>,
    val description: String,
    val context: String?,
    val challenges: String?,
    val goals: String?,
    val isArchived: Boolean
)

fun Action.toResource(): ActionResource =
    ActionResource(
        id = id.value.toString(),
        attributes = ActionResourceAttributes(
            assessmentId = this.assessmentId.uuid.toString(),
            title = title.value,
            targetQuestionsToThinkAbout = targetQuestionsToThinkAbout.value.map { it.toResource() },
            targetTclDrivers = targetTclDrivers.value.map { it.toResource() },
            description = description.value,
            context = context?.value,
            challenges = challenges?.value,
            goals = goals?.value,
            isArchived = isArchived
        )
    )
