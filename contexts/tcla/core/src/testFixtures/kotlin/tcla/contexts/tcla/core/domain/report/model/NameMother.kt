package tcla.contexts.tcla.core.domain.report.model

object NameMother {
    fun default(value: String = "Task complexity"): Name =
        Name(
            value = value
        )
}
