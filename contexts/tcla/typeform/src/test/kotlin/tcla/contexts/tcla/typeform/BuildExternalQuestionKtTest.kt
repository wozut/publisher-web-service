package tcla.contexts.tcla.typeform

import com.google.gson.JsonParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import tcla.contexts.tcla.core.domain.answeroption.AnswerOptionMother
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestionMother
import tcla.contexts.tcla.core.domain.question.model.Question
import java.util.UUID

class BuildExternalQuestionKtTest {
    @Test
    fun `successfully build opinion-scale external question when question is multiple choice with scale from 1 to 5`() {
        val id = UUID.randomUUID()
        val order = 1
        val teamCompositionLabel = Question.Label.Q1TeamCompositionComplexity1
        val opinionScaleMultipleChoiceQuestion = MultipleChoiceQuestionMother.build(id = id, order = order, label = teamCompositionLabel)

        val opinionScaleExternalQuestionType = """opinion_scale"""
        val expectedOpinionScaleExternalQuestion = """
            {
                "title": "${teamCompositionLabel.value.replace("\"", "\\\"")}",
                "ref": "tcl-question-${order}",
                "properties": {
                    "start_at_one": true,
                    "steps": ${opinionScaleMultipleChoiceQuestion.answerOptions.size},
                    "labels": {
                        "left": "Strongly disagree",
                        "right": "Strongly agree"
                    }
                },
                "validations": {
                    "required": true
                },
                "type": "$opinionScaleExternalQuestionType"
            }
        """.trimIndent()

        val actualExternalQuestion = opinionScaleMultipleChoiceQuestion.toExternalQuestion()
        assertThat(actualExternalQuestion).isEqualTo(expectedOpinionScaleExternalQuestion)
    }

    @TestFactory
    fun `Build external questions`(): List<DynamicTest> = listOf(
            genderMultipleChoiceQuestion to expectedGenderExternalQuestion,
            modeOfWorkingMultipleChoiceQuestion to expectedModeOfWorkingExternalQuestion,
        ).map {(input, expected ) ->
            DynamicTest.dynamicTest("Successfully build multiple_choice external question when question alias is Gender or Mode_Of_Working") {
                val actualExternalQuestion = input.toExternalQuestion()

                val expectedJson = JsonParser.parseString(expected)
                val actualJson = JsonParser.parseString(actualExternalQuestion)
                assertThat(actualJson).isEqualTo(expectedJson)
            }
        }

    companion object {
        val id = UUID.randomUUID()
        val order = 1
        val label = Question.Label.Gender
        val mutipleChoiceExternalQuestionType = "multiple_choice"

        val genderAnswerOptions = setOf(
            AnswerOptionMother.genderFemale(),
            AnswerOptionMother.genderMale(),
            AnswerOptionMother.genderOther()
        )

        val modeOfWorkingAnswerOptions = setOf(
            AnswerOptionMother.modeOfWorkingHybrid(),
            AnswerOptionMother.modeOfWorkingRemote(),
            AnswerOptionMother.modeOfWorkingInPerson()
        )
        val genderMultipleChoiceQuestion = MultipleChoiceQuestionMother.build(id = id, order = order, label = label, answerOptions = genderAnswerOptions)
        val modeOfWorkingMultipleChoiceQuestion = MultipleChoiceQuestionMother.build(id = id, order = order, label = label, answerOptions = modeOfWorkingAnswerOptions)
        val expectedGenderExternalQuestion = """
            {
                "title": "${label.value.replace("\"", "\\\"")}",
                "ref": "tcl-question-${order}",
                "properties": {
                    "randomize": false,
                    "allow_multiple_selection": false,
                    "allow_other_choice": false,
                    "vertical_alignment": false,
                    "choices": [
                        ${genderAnswerOptions.joinToString(",") {
                            """
                                {
                                "ref": "choice-${it.order.value}",
                                "label": "${it.value.value}"
                            }
                            """.trimIndent()
                        }
                        }
                    ]
                },
                "validations": {
                    "required": true
                },
                "type": "$mutipleChoiceExternalQuestionType"
            }
        """.trimIndent()

        val expectedModeOfWorkingExternalQuestion = """
            {
                "title": "${label.value.replace("\"", "\\\"")}",
                "ref": "tcl-question-${order}",
                "properties": {
                    "randomize": false,
                    "allow_multiple_selection": false,
                    "allow_other_choice": false,
                    "vertical_alignment": false,
                    "choices": [
                        ${
                            modeOfWorkingAnswerOptions.joinToString(",") {
                            """
                                {
                                "ref": "choice-${it.order.value}",
                                "label": "${it.value.value}"
                            }
                            """.trimIndent()
                        }
                        }
                    ]
                },
                "validations": {
                    "required": true
                },
                "type": "$mutipleChoiceExternalQuestionType"
            }
        """.trimIndent()
    }
}
