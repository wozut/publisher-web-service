package tcla.contexts.tcla.webapi.springweb.report.jsonapi

import tcla.contexts.tcla.core.domain.report.model.BodyPart

data class BodyPartResource(
    val text: String,
    val order: Int
)

fun BodyPart.toResource(): BodyPartResource =
    BodyPartResource(
        text = text.value,
        order = order.value
    )
