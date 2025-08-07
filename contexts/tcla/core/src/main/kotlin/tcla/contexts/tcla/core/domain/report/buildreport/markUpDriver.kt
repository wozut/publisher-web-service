package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Driver

fun Driver.markUp(): String = "<driver entry-key=\"${id}\" cluster-id=\"${cluster.id}\">${name}</driver>"
