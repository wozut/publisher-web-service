package tcla.contexts.tcla.core.domain.tcldriverscore.model

@JvmInline
value class TclDriverScoreValue(val value: Double) : Comparable<TclDriverScoreValue> {
    override fun compareTo(other: TclDriverScoreValue): Int = value.compareTo(other.value)
}
