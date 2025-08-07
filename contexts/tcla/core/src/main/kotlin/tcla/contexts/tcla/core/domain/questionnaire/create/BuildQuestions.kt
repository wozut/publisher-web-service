package tcla.contexts.tcla.core.domain.questionnaire.create

import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOptionId
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestion
import tcla.contexts.tcla.core.domain.question.model.Order
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.question.model.QuestionLabels
import tcla.contexts.tcla.core.domain.question.model.QuestionId
import java.util.UUID

private val optionValuesFrom1to5 = listOf("1", "2", "3", "4", "5")

fun buildQuestions(
    includeGenderQuestion: Boolean,
    includeWorkFamiliarityQuestion: Boolean,
    includeTeamFamiliarityQuestion: Boolean,
    includeModeOfWorkingQuestion: Boolean
): Set<Question> {
    val questions: MutableSet<Question> = mutableSetOf()

    if(includeGenderQuestion) questions.add(buildGenderQuestion(1))
    if(includeWorkFamiliarityQuestion) questions.add(buildWorkFamiliarityQuestion(questions.size + 1))
    if(includeTeamFamiliarityQuestion) questions.add(buildTeamFamiliarityQuestion(questions.size + 1))
    if(includeModeOfWorkingQuestion) questions.add(buildModeOfWorkingQuestion(questions.size + 1))

    questions.addAll(buildDriverQuestions(questions.size + 1))

    return questions.toSet()
}


private fun buildDriverQuestions(firstOrder: Int): Set<MultipleChoiceQuestion> {
    return QuestionLabels.driverQuestionLabels.mapIndexed { index, alias ->
        buildMultipleChoiceQuestion(alias, firstOrder + index, optionValuesFrom1to5)
    }.toSet()
}

private fun buildModeOfWorkingQuestion(order: Int) = buildMultipleChoiceQuestion(
    Question.Label.ModeOfWorking,
    order,
    optionValues = listOf("Remote", "In-person", "Hybrid")
)

private fun buildTeamFamiliarityQuestion(order: Int) = buildMultipleChoiceQuestion(
    Question.Label.TeamFamiliarity,
    order,
    optionValues = optionValuesFrom1to5
)

private fun buildWorkFamiliarityQuestion(order: Int) = buildMultipleChoiceQuestion(
    Question.Label.WorkFamiliarity,
    order,
    optionValues = optionValuesFrom1to5
)

private fun buildGenderQuestion(order: Int) = buildMultipleChoiceQuestion(
    Question.Label.Gender,
    order,
    optionValues = listOf("Female", "Male", "Other")
)

private fun buildMultipleChoiceQuestion(label: Question.Label, order: Int, optionValues: List<String>): MultipleChoiceQuestion =
    MultipleChoiceQuestion(
        id = QuestionId(UUID.randomUUID()),
        order = Order(order),
        answerOptions = buildAnswerOptions(optionValues),
        label = label
    )

private fun buildAnswerOptions(optionValues: List<String>): Set<AnswerOption> =
    optionValues.mapIndexed { index, option -> buildAnswerOption(index, option) }.toSet()

private fun buildAnswerOption(index: Int, option: String): AnswerOption =
    AnswerOption(
        id = AnswerOptionId(UUID.randomUUID()),
        order = AnswerOption.Order(value = index + 1),
        value = AnswerOption.Value(value = option)
    )
