package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.report.model.Cluster

class MarkUpClusterKtTest {
    @Test
    fun `it generates markup`() {
        val cluster = Cluster.TeamCharacteristics
        val id = cluster.id
        val name = cluster.name
        val expectedResult = "<cluster entry-key=\"$id\" cluster-id=\"$id\">$name</cluster>"

        val actualResult = cluster.markUp()

        assertThat(actualResult).isEqualTo(expectedResult)
    }
}
