package tcla.contexts.tcla.core.domain.report.model

object TitleMother {
    fun default(
        value: String = "Title"
    ): Title =
        Title(
            value = value
        )
}
