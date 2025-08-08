package tcla.contexts.tcla.core.domain.report.model

object SectionMother {
    fun default(
            title: Title = TitleMother.default(),
            body: List<BodyPart> = listOf(BodyPartMother.default()),
            tclDriverAndScoreList: List<TclDriverAndScore> = listOf(TclDriverAndScoreMother.default())
    ): Section = Section(
        title = title,
        body = body,
        tclDriverAndScoreList = tclDriverAndScoreList
    )
}
