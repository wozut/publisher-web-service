package tcla.contexts.tcla.core.domain.message.accountmessage

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import java.time.Instant

interface AccountMessageRepository {

    fun find(accountMessageId: AccountMessageId): Either<NonEmptyList<Failure>, AccountMessage>
    fun saveAll(accountMessages: List<AccountMessage>): Either<NonEmptyList<Failure>, Unit>
    fun save(accountMessage: AccountMessage): Either<NonEmptyList<Failure>, AccountMessage>
    fun searchByActuallySentAtIsNullAndScheduledToBeSentAtBefore(instant: Instant): Either<NonEmptyList<Failure>, List<AccountMessage>>
    fun searchBySurveyIdAndActuallySentAtIsNull(surveyId: QuestionnaireId): Either<NonEmptyList<Failure>, List<AccountMessage>>
    fun deleteAll(ids: List<AccountMessageId>): Either<Failure, Unit>
}
