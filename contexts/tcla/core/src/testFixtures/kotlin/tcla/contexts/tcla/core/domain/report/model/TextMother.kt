package tcla.contexts.tcla.core.domain.report.model

object TextMother {
    fun default(value: String = "text"): BodyPart.Text =
        BodyPart.Text(
            value = value
        )
}
