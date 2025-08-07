package tcla.contexts.tcla.webapi.springweb.report.jsonapi

import tcla.contexts.tcla.core.domain.report.model.BodyPart
import tcla.contexts.tcla.core.domain.report.model.Section
import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore

data class SectionResource(
    val title: String,
    val body: List<BodyPartResource>,
    val tclDriverScores: List<TclDriverAndScoreResource>
)

fun Section.toResource(): SectionResource =
    SectionResource(
        title = title.value,
        body = body.map(BodyPart::toResource),
        tclDriverScores = tclDriverAndScoreList.map(TclDriverAndScore::toResource)
    )




