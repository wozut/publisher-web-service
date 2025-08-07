package tcla.contexts.tcla.webapi.springweb.response.jsonapi

import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling


data class ResponseResource(
    val id: String,
    val attributes: ResponseResourceAttributes,
) {
    val type: String = RESPONSE_JSON_API_TYPE
}

fun QuestionnaireFilling.toResource(): ResponseResource =
    ResponseResource(
        id = id.uuid.toString(),
        attributes = ResponseResourceAttributes()
    )
