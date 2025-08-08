package tcla.contexts.tcla.core.domain.message.accountmessage.model

import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import java.time.Instant
import java.util.Hashtable

data class AccountMessage(
    val id: AccountMessageId,
    val channel: Channel,
    val type: Type,
    val scheduledToBeSentAt: Instant,
    val actuallySentAt: Instant? = null,
    val accountId: String,
    val surveyId: QuestionnaireId,
    val extraData: Hashtable<String, String> = Hashtable()
) {
    enum class Type {
        _48_HOURS_BEFORE_SURVEY_END, RESULTS_AVAILABLE, RESULTS_NOT_AVAILABLE, CANCELED_SURVEY
    }
    fun markAsSentAt(instant: Instant): AccountMessage = copy(actuallySentAt = instant)
}
