package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.core.domain.report.toEnumeration

fun Iterable<Driver>.markUpDriverEnumeration(): String = map(Driver::markUp).toEnumeration()
