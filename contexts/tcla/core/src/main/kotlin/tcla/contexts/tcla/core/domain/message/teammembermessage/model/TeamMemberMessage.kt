package tcla.contexts.tcla.core.domain.message.teammembermessage.model

import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.contexts.tcla.core.domain.teammember.model.TeamMemberId
import java.time.Instant
import java.util.Hashtable

data class TeamMemberMessage(
    val id: TeamMemberMessageId,
    val channel: Channel,
    val type: Type,
    val scheduledToBeSentAt: Instant,
    val actuallySentAt: Instant? = null,
    val teamMemberId: TeamMemberId,
    val respondentId: RespondentId?,
    val surveyId: QuestionnaireId,
    val extraData: Hashtable<String, String> = Hashtable()
) {
    enum class Type {
        SURVEY_INVITATION, _48_HOURS_BEFORE_SURVEY_END, SURVEY_DURATION_EXTENDED
    }

    fun markAsSentAt(instant: Instant): TeamMemberMessage = copy(actuallySentAt = instant)
}
