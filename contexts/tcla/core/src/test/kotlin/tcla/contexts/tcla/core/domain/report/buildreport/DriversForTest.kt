package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverName
import java.util.UUID

object DriversForTest {
    val relevantDrivers = RelevantDriverNamesForTests.all
        .map {
            TclDriver(
                id = TclDriverId(UUID.fromString("ba85d3fc-3ff1-42d4-ae2f-e0562817e2a9")),
                name = TclDriverName(value = it),
                children = emptyList(),
                parent = null
            )
        }

    val clusters = ClusterNamesForTests.all.map {
        TclDriver(
            id = TclDriverId(UUID.fromString("ba85d3fc-3ff1-42d4-ae2f-e0562817e2a9")),
            name = TclDriverName(value = it),
            children = emptyList(),
            parent = null
        )
    }

    val nonRelevantDrivers = NonRelevantDriverNamesForTests.all
        .map {
            TclDriver(
                id = TclDriverId(UUID.fromString("ba85d3fc-3ff1-42d4-ae2f-e0562817e2a9")),
                name = TclDriverName(value = it),
                children = emptyList(),
                parent = null
            )
        }

    val all = clusters.plus(relevantDrivers).plus(nonRelevantDrivers)
}
