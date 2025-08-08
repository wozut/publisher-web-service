package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.core.domain.report.model.Drivers

fun String.toRelevantDriver(): Driver? = Drivers.relevantDrivers.find { relevantDriver -> relevantDriver.name == this }
