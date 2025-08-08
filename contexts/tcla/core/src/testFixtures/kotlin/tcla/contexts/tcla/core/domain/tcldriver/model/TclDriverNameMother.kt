package tcla.contexts.tcla.core.domain.tcldriver.model

object TclDriverNameMother {
    fun default(value: String = "Driver Name"): TclDriverName =
        TclDriverName(value = value)
}
