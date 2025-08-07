package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Driver

fun Set<Pair<Driver, List<String>>>.chooseQuestionsFor(drivers: List<Driver>): List<String> =
    when (drivers.size) {
        1 -> this.getTclDriverQuestionsFor(drivers.first())
        in 2..4 -> drivers.map { this.getTclDriverQuestionsFor(it).take(2) }.flatten()
        in 5..Int.MAX_VALUE -> drivers.map { this.getTclDriverQuestionsFor(it).take(1) }.flatten()
        else -> listOf()
    }
