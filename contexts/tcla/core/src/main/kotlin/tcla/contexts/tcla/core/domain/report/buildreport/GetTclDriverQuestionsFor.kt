package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Driver


fun Set<Pair<Driver, List<String>>>.getTclDriverQuestionsFor(driver: Driver): List<String> =
    this.filter { it.first == driver }
        .let { it: List<Pair<Driver, List<String>>> ->
            when (it.isNotEmpty()) {
                true -> it.first().second
                false -> emptyList()
            }
        }
