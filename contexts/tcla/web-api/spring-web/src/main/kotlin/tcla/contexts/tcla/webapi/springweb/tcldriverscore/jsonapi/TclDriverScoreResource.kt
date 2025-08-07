package tcla.contexts.tcla.webapi.springweb.tcldriverscore.jsonapi

import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore


data class TclDriverScoreResource(
    val id: String,
    val attributes: TclDriverScoreResourceAttributes,
) {
    val type: String = TCL_DRIVER_SCORE_JSON_API_TYPE
}

fun TclDriverScore.toResource(): TclDriverScoreResource =
    TclDriverScoreResource(
        id = id.value.toString(),
        attributes = TclDriverScoreResourceAttributes(
            value = value.value,
            tclDriverId = tclDriverId.value.toString(),
            assessmentId = assessmentId.uuid.toString()
        )
    )
