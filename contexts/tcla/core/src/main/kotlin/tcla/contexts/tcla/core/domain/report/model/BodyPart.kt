package tcla.contexts.tcla.core.domain.report.model


data class BodyPart(
    val text: Text,
    val order: Order
) {
    @JvmInline
    value class Text(val  value: String)

    @JvmInline
    value class Order(val value: Int)
}
