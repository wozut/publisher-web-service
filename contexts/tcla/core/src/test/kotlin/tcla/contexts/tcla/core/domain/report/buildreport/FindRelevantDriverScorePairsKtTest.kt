package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreMother

class FindRelevantDriverScorePairsKtTest {
    @Test
    fun `it finds all relevant drivers`() {
        val allDrivers = DriversForTest.all
        val tclDriverScore = TclDriverScoreMother.default()
        val pairs: List<Pair<TclDriver, TclDriverScore>> = allDrivers.map { Pair(it, tclDriverScore) }

        val expectedTclDriverScorePairs = DriversForTest.relevantDrivers
            .map { Pair(it, tclDriverScore) }

        val actual = pairs.findRelevantDriverScorePairs()

        assertThat(actual).containsExactlyInAnyOrder(*expectedTclDriverScorePairs.toTypedArray())
    }
}
