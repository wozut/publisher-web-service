package tcla.contexts.tcla.core.domain.message.teammembermessage

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import java.time.Instant

interface TeamMemberMessageRepository {
    fun find(teamMemberMessageId: TeamMemberMessageId): Either<NonEmptyList<Failure>, TeamMemberMessage>
    fun saveAll(teamMemberMessages: List<TeamMemberMessage>): Either<NonEmptyList<Failure>, Unit>
    fun save(teamMemberMessage: TeamMemberMessage): Either<NonEmptyList<Failure>, TeamMemberMessage>
    fun searchByActuallySentAtIsNullAndScheduledToBeSentAtBefore(instant: Instant): Either<NonEmptyList<Failure>, List<TeamMemberMessage>>
    fun searchBySurveyIdAndActuallySentAtIsNull(surveyId: QuestionnaireId): Either<NonEmptyList<Failure>, List<TeamMemberMessage>>
    fun deleteAll(ids: List<TeamMemberMessageId>): Either<Failure, Unit>
}
