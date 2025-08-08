package tcla.contexts.tcla.core.domain.report.model

object BodyPartMother {
    fun default(
        text: BodyPart.Text = TextMother.default(),
        order: BodyPart.Order = OrderMother.default()
    ): BodyPart =
        BodyPart(
            text = text,
            order = order
        )
}
