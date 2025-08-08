package tcla.contexts.tcla.core.domain.tcldriver.model

import java.util.UUID

object TclDriverIdMother {
    fun default(value: UUID = UUID.randomUUID()): TclDriverId =
        TclDriverId(value = value)
}
