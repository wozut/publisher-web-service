package tcla.contexts.tcla.core.domain.report.model

import java.util.UUID

object ReportIdMother {
    fun default(
        value: UUID = UUID.randomUUID()
    ): ReportId = ReportId(
        value = value
    )

}
