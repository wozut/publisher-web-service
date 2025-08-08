package tcla.contexts.tcla.core.domain.tcldriverscore.model

import java.util.UUID

object TclDriverScoreIdMother {
    fun default(
        value: UUID = UUID.randomUUID()
    ): TclDriverScoreId = TclDriverScoreId(
        value = value
    )

}
