package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.report.model.Term

class MarkUpTermKtTest {

    @Test
    fun `it generates markup`() {
        val term = Term.Cluster
        val id = term.id
        val children = "children"
        val expectedResult = "<term entry-key=\"${id}\">$children</term>"

        val actualResult = term.markUp(children)

        Assertions.assertThat(actualResult).isEqualTo(expectedResult)
    }
}
