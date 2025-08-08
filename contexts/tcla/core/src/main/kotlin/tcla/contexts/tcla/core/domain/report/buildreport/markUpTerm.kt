package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Term

fun Term.markUp(children: String): String = "<term entry-key=\"${id}\">$children</term>"
