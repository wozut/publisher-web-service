package tcla.contexts.tcla.core.domain.report

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EnglishGeneratorTest {

    @Test
    fun `it creates a enumeration of two words`() {
        val expectedSequence = "pear and lemon"
        val actualSequence = listOf("pear", "lemon").toEnumeration()
        assertThat(actualSequence).isEqualTo(expectedSequence)
    }
    @Test
    fun `it creates a enumeration of more than two words`() {
        val expectedSequence = "pear, orange, and lemon"
        val actualSequence = listOf("pear", "orange" , "lemon").toEnumeration()
        assertThat(actualSequence).isEqualTo(expectedSequence)
    }
}
