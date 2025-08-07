package tcla.contexts.tcla.webapi.springweb.report.jsonapi

import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore

data class TclDriverAndScoreResource(
    val id: String?,
    val name: String,
    val score: Double
)

fun TclDriverAndScore.toResource(): TclDriverAndScoreResource =
    TclDriverAndScoreResource(
        id = id,
        name = name.value,
        score = score.value
    )
