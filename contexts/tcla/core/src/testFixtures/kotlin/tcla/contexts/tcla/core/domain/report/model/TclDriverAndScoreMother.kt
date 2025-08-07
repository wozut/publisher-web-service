package tcla.contexts.tcla.core.domain.report.model


object TclDriverAndScoreMother {
    fun default(
        id: String? = Driver.Consistency.id,
        name: Name = NameMother.default(),
        score: Score = ScoreMother.default()
    ): TclDriverAndScore =
        TclDriverAndScore(
            id = id,
            name = name,
            score = score
        )
}
