package tcla.contexts.tcla.core.domain.report.model

data class Sections(
    val quickSummary: Section,
    val introduction: Section,
    val overview: Section,
    val analysis: List<Section>,
    val annex: List<Section>
)
