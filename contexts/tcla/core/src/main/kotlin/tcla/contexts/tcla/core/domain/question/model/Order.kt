package tcla.contexts.tcla.core.domain.question.model

@JvmInline
value class Order(val value: Int) : Comparable<Order> {
    override fun compareTo(other: Order): Int = this.value.compareTo(other.value)
}
