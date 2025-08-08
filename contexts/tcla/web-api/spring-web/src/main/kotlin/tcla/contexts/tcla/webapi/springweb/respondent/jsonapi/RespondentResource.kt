package tcla.contexts.tcla.webapi.springweb.respondent.jsonapi

import tcla.contexts.tcla.core.domain.respondent.model.Respondent


data class RespondentResource(
    val id: String,
    val attributes: RespondentResourceAttributes,
) {
    val type: String = RESPONDENT_JSON_API_TYPE
}

fun Respondent.toResource(): RespondentResource =
    RespondentResource(
        id = id.uuid.toString(),
        attributes = RespondentResourceAttributes(
            name = name,
            email = email.string,
            assessmentId = assessmentId.uuid.toString()
        )
    )
