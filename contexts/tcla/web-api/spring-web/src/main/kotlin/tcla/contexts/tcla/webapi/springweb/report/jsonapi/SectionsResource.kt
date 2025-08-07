package tcla.contexts.tcla.webapi.springweb.report.jsonapi

import tcla.contexts.tcla.core.domain.report.model.Section
import tcla.contexts.tcla.core.domain.report.model.Sections

data class SectionsResource(
    val quickSummary: SectionResource,
    val introduction: SectionResource,
    val overview: SectionResource,
    val analysis: List<SectionResource>,
    val annex: List<SectionResource>
)

fun Sections.toResource(): SectionsResource =
    SectionsResource(
        quickSummary = quickSummary.toResource(),
        introduction = introduction.toResource(),
        overview = overview.toResource(),
        analysis = analysis.map(Section::toResource),
        annex = annex.map(Section::toResource)
    )


