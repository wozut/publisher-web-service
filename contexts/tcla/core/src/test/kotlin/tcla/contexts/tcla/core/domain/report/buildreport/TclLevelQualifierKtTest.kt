package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TclLevelQualifierKtTest {

    @Test
    fun `TCL Level Qualification label must be SLIGHTLY CONCERNING when TCL Level score average is equal or greater than 3(Double)`() {

        val actualOverviewText = getTclLevelQualificationLabel(3.0)

        val expectedTclLevelQualificationLabel = "slightly concerning"
        assertThat(actualOverviewText).isEqualTo(expectedTclLevelQualificationLabel)
    }

    @Test
    fun `TCL Level Qualification label must be WITHIN A REASONABLE RANGE when TCL Level score average is BETWEEN 1 point 5 and 3, not included`() {

        //TODO: add case for  1.6
        val actualOverviewText = getTclLevelQualificationLabel(2.9)

        val expectedTclLevelQualificationLabel = "within a reasonable range"
        assertThat(actualOverviewText).isEqualTo(expectedTclLevelQualificationLabel)
    }

    @Test
    fun `TCL Level Qualification label must be UNDER CONTROL when TCL Level score average is BELOW OR EQUAL TO 1 point 5`() {

        val actualOverviewText = getTclLevelQualificationLabel(1.5)

        val expectedTclLevelQualificationLabel = "under control"
        assertThat(actualOverviewText).isEqualTo(expectedTclLevelQualificationLabel)
    }

}
