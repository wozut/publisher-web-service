package tcla.contexts.tcla.core.domain.report.model

data class Section(
    val title: Title,
    val body: List<BodyPart>,
    val tclDriverAndScoreList: List<TclDriverAndScore> = listOf()
)
