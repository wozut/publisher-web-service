package tcla.contexts.tcla.typeform

import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestion
import tcla.contexts.tcla.core.domain.question.model.Order
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.libraries.jsonserialization.escaped

internal fun Question.toExternalQuestion() = when (this) {
    is MultipleChoiceQuestion -> when (this.label) {
        in setOf(Question.Label.Gender, Question.Label.ModeOfWorking) -> this.toMultipleChoiceQuestion()
        else -> this.toOpinionScaleQuestion()
    }
}

private fun MultipleChoiceQuestion.toOpinionScaleQuestion() =
    """
        {
            "title": "${buildTitle(this).escaped()}",
            "ref": "${buildQuestionRef(this.order).escaped()}",
            "properties": {
                "start_at_one": true,
                "steps": ${this.answerOptions.size},
                "labels": {
                    "left": "Strongly disagree",
                    "right": "Strongly agree"
                }
            },
            "validations": {
                "required": true
            },
            "type": "opinion_scale"
        }
    """.trimIndent()

private fun MultipleChoiceQuestion.toMultipleChoiceQuestion() =
    """
        {
            "title": "${buildTitle(this).escaped()}",
            "ref": "${buildQuestionRef(this.order).escaped()}",
            "properties": {
                "randomize": false,
                "allow_multiple_selection": false,
                "allow_other_choice": false,
                "vertical_alignment": false,
                "choices": [
                    ${this.answerOptions.toChoices()}
                ]
            },
            "validations": {
                "required": true
            },
            "type": "multiple_choice"
        }
    """.trimIndent()

private fun Set<AnswerOption>.toChoices(): String =
    toList()
        .sortedBy { it.order }
        .joinToString(separator = ",") { answerOption -> answerOption.toChoice() }

private fun AnswerOption.toChoice(): String =
    """
    {
        "ref": "${buildChoiceRef(order).escaped()}",
        "label": "${toChoiceLabel().escaped()}"
    }
    """.trimIndent()

private fun AnswerOption.toChoiceLabel() = value.value

private fun buildTitle(question: Question) = question.label.value

private fun buildChoiceRef(answerOptionOrder: AnswerOption.Order): String =
    "choice-${calculateChoiceOrder(answerOptionOrder)}"
private fun buildQuestionRef(order: Order): String = "tcl-question-${calculateQuestionOrder(order)}"

private fun calculateQuestionOrder(order: Order): Int = order.value
private fun calculateChoiceOrder(order: AnswerOption.Order): Int = order.value
