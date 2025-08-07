package tcla.contexts.tcla.core.domain.report.buildreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.report.model.Driver

class ChooseQuestionsForKtTest {
    @Test
    fun `when there is exactly 1 driver, then list all questions for that driver`() {
        val question1 = "question 1"
        val question2 = "question 2"
        val expectedQuestions = listOf(question1, question2)

        val driver = Driver.TeamComplexity

        val driverQuestionsPairs = setOf(Pair(driver, listOf(question1, question2)))

        val actualQuestions = driverQuestionsPairs.chooseQuestionsFor(listOf(driver))

        assertThat(actualQuestions).containsExactly(*expectedQuestions.toTypedArray())
    }

    @Test
    fun `when there are between 2 and 4 drivers, then list 2 questions for each driver`() {
        val question1 = "question 1"
        val question2 = "question 2"
        val question3 = "question 3"
        val question4 = "question 4"
        val expectedQuestions = listOf(question1, question2, question3, question4)

        val driver1 = Driver.TeamComplexity
        val driver2 = Driver.Environment

        val driverQuestionsPairs = setOf(
            Pair(driver1, listOf(question1, question2)),
            Pair(driver2, listOf(question3, question4))
        )

        val actualQuestions = driverQuestionsPairs.chooseQuestionsFor(listOf(driver1, driver2))

        assertThat(actualQuestions).containsExactly(*expectedQuestions.toTypedArray())
    }

    @Test
    fun `when there are 5 or more drivers, then list 1 question for each driver`() {
        val question1 = "question 1"
        val question2 = "question 2"
        val question3 = "question 3"
        val question4 = "question 4"
        val question5 = "question 5"
        val expectedQuestions = listOf(question1, question2, question3, question4, question5)

        val driver1 = Driver.TeamComplexity
        val driver2 = Driver.Environment
        val driver3 = Driver.ToolPerformance
        val driver4 = Driver.ToolSuitability
        val driver5 = Driver.Resilience

        val driverQuestionsPairs = setOf(
            Pair(driver1, listOf(question1)),
            Pair(driver2, listOf(question2)),
            Pair(driver3, listOf(question3)),
            Pair(driver4, listOf(question4)),
            Pair(driver5, listOf(question5))
        )

        val actualQuestions =
            driverQuestionsPairs.chooseQuestionsFor(listOf(driver1, driver2, driver3, driver4, driver5))

        assertThat(actualQuestions).containsExactly(*expectedQuestions.toTypedArray())
    }
}
