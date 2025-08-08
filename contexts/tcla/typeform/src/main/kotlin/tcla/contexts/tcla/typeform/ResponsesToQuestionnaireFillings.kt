package tcla.contexts.tcla.typeform

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import tcla.contexts.tcla.core.domain.answer.model.Answer
import tcla.contexts.tcla.core.domain.answer.model.AnswerId
import tcla.contexts.tcla.core.domain.answer.model.MultipleChoiceAnswer
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestion
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.questionnairefilling.model.ExternalId
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFillingId
import java.util.UUID

fun responsesToQuestionnaireFillings(responseBody: String, questions: Set<Question>): Set<QuestionnaireFilling> =
    JsonParser.parseString(responseBody)
        .asJsonObject["items"]
        .asJsonArray
        .filterNot { jsonElement -> jsonElement.extractAnswers().isJsonNull }
        .map { response -> response.toQuestionnaireFilling(questions) }.toSet()

private fun JsonElement.toQuestionnaireFilling(questions: Set<Question>): QuestionnaireFilling =
    QuestionnaireFilling(
        id = QuestionnaireFillingId(UUID.randomUUID()),
        answers = this.extractAnswers().asJsonArray.toAnswers(questions),
        externalId = ExternalId(this.asJsonObject["response_id"].asString)
    )

private fun JsonElement.extractAnswers(): JsonElement = asJsonObject["answers"]

private fun JsonArray.toAnswers(questions: Set<Question>): Set<Answer> =
    this.mapIndexed { index, elem -> elem.asJsonObject.toAnswer(index, questions) }.toSet()

private fun JsonObject.toAnswer(index: Int, questions: Set<Question>): Answer {
    val type = this["type"].asString
    val question: MultipleChoiceQuestion? = questions
        .filterIsInstance<MultipleChoiceQuestion>()
        .find { it.order.value == index + 1 }

    if(question == null) TODO()

    val answerOption: AnswerOption? = extractAnswerOptionOrderByQuestionType(type)
        .let { order: Int ->
            question.answerOptions.find { it.order.value == order }
        }

    if (answerOption == null) TODO()

    return MultipleChoiceAnswer(
        id = AnswerId(UUID.randomUUID()),
        answerOption = answerOption
    )
}

private fun JsonObject.extractAnswerOptionOrderByQuestionType(type: String): Int =
    when (type) {
        "choice" -> this["choice"].asJsonObject["ref"].asString.split("-").last().toInt()
        "number" -> this["number"].asInt
        else -> TODO()
    }


