package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.report.model.Section
import tcla.contexts.tcla.core.domain.report.model.Title
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

@Named
class BuildQuickSummary(
    private val buildQuickSummaryBody: BuildQuickSummaryBody
) {

    companion object {
        const val TOO_MANY_HIGH_DRIVERS_THRESHOLD: Int = 4
    }

    fun execute(
        tclDriverScorePairs: List<Pair<TclDriver, TclDriverScore>>,
        range: Double
    ): Section {
        val highestRelevantDrivers = tclDriverScorePairs.findRelevantDriverScorePairsWithHighestScore(range)
        val tclDriverNames = highestRelevantDrivers.map { it.name.value }
        return Section(
            title = Title("# Quick summary"),
            body = buildQuickSummaryBody.execute(tclDriverNames, TOO_MANY_HIGH_DRIVERS_THRESHOLD),
            tclDriverAndScoreList = highestRelevantDrivers
        )
    }

}
