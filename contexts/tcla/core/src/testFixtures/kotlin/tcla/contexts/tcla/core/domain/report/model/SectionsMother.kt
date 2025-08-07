package tcla.contexts.tcla.core.domain.report.model

object SectionsMother {
    fun default(
        quickSummary: Section = SectionMother.default(),
        introduction: Section = SectionMother.default(),
        overview: Section = SectionMother.default(),
        analysis: List<Section> = listOf(SectionMother.default()),
        annex: List<Section> = listOf(SectionMother.default())
    ): Sections = Sections(
        quickSummary = quickSummary,
        introduction = introduction,
        overview = overview,
        analysis = analysis,
        annex = annex
    )
}
