package tcla.contexts.tcla.core.domain.report.model

object ScoreMother {
    fun default(value: Double = 3.0): Score =
        Score(
            value = value
        )
}
