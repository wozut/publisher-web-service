package tcla.contexts.tcla.core.domain.answeroption.model

data class AnswerOption(
    val id: AnswerOptionId,
    val value: Value,
    val order: Order
) {
    @JvmInline
    value class Value(val value: String)
    @JvmInline
    value class Order(val value: Int):  Comparable<Order> {
        override fun compareTo(other: Order): Int = this.value.compareTo(other.value)
    }
}
