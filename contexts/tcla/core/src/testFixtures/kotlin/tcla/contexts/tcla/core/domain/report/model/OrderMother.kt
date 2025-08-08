package tcla.contexts.tcla.core.domain.report.model

object OrderMother {
    fun default(value: Int = 1): BodyPart.Order =
        BodyPart.Order(
            value = value
        )
}
