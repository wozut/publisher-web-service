package tcla.contexts.tcla.database.springdatajpa.message.accountmessage

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.message.accountmessage.AccountMessageRepository
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaire
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaireRepository
import java.sql.Timestamp
import java.time.Instant

@Named
class SqlAccountMessageRepository(
    private val jpaAccountMessageRepository: JpaAccountMessageRepository,
    private val jpaQuestionnaireRepository: JpaQuestionnaireRepository
) : AccountMessageRepository {
    override fun find(accountMessageId: AccountMessageId): Either<NonEmptyList<Failure>, AccountMessage> =
        Either.catch { jpaAccountMessageRepository.findByIdOrNull(accountMessageId.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaAccountMessage: JpaAccountMessage? ->
                when (jpaAccountMessage) {
                    null -> Failure.EntityNotFound.AccountMessage.nel().left()
                    else -> jpaAccountMessage.toDomain()
                }
            }

    override fun saveAll(accountMessages: List<AccountMessage>): Either<NonEmptyList<Failure>, Unit> =
        accountMessages.mapOrAccumulate { accountMessage: AccountMessage ->
            Either.catch { jpaQuestionnaireRepository.findByIdOrNull(accountMessage.surveyId.uuid) }
                .mapLeft { Failure.DatabaseException(it) }
                .flatMap { jpaQuestionnaire: JpaQuestionnaire? ->
                    when (jpaQuestionnaire) {
                        null -> Failure.EntityNotFound.Survey.left()
                        else -> jpaQuestionnaire.right()
                    }
                }.flatMap { jpaQuestionnaire: JpaQuestionnaire ->
                    accountMessage.toJpa(jpaQuestionnaire = jpaQuestionnaire)
                        .mapLeft { Failure.UnableToTransformIntoPersistenceData.AccountMessage }
                }.bind()
        }.flatMap { jpaAccountMessages: List<JpaAccountMessage> ->
            Either.catch { jpaAccountMessageRepository.saveAll(jpaAccountMessages) }
                .mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
        }.flatMap { Unit.right() }

    override fun save(accountMessage: AccountMessage): Either<NonEmptyList<Failure>, AccountMessage> =
        Either.catch { jpaQuestionnaireRepository.findByIdOrNull(accountMessage.surveyId.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaQuestionnaire: JpaQuestionnaire? ->
                when (jpaQuestionnaire) {
                    null -> Failure.EntityNotFound.Questionnaire.nel().left()
                    else -> jpaQuestionnaire.right()
                }
            }.flatMap { jpaQuestionnaire: JpaQuestionnaire ->
                accountMessage.toJpa(jpaQuestionnaire = jpaQuestionnaire)
                    .mapLeft { Failure.UnableToTransformIntoPersistenceData.AccountMessage.nel() }
            }.flatMap { jpaAccountMessage: JpaAccountMessage ->
                Either.catch { jpaAccountMessageRepository.save(jpaAccountMessage) }
                    .mapLeft { Failure.DatabaseException(it).nel() }
            }.flatMap { jpaAccountMessage -> jpaAccountMessage.toDomain() }

    override fun searchByActuallySentAtIsNullAndScheduledToBeSentAtBefore(instant: Instant): Either<NonEmptyList<Failure>, List<AccountMessage>> =
        Either.catch {
            jpaAccountMessageRepository.findAllByActuallySentAtIsNullAndScheduledToBeSentAtBefore(
                Timestamp(instant.toEpochMilli())
            )
        }.mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
            .flatMap { jpaAccountMessages: List<JpaAccountMessage> ->
                jpaAccountMessages.mapOrAccumulate { jpaAccountMessage: JpaAccountMessage ->
                    jpaAccountMessage.toDomain().bindNel()
                }
            }

    override fun searchBySurveyIdAndActuallySentAtIsNull(surveyId: QuestionnaireId): Either<NonEmptyList<Failure>, List<AccountMessage>> =
        Either.catch { jpaAccountMessageRepository.findAllByQuestionnaire_IdAndActuallySentAtIsNull(surveyId.uuid) }
            .mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
            .flatMap { jpaAccountMessages ->
                jpaAccountMessages.mapOrAccumulate { jpaAccountMessage -> jpaAccountMessage.toDomain().bindNel() }
            }

    override fun deleteAll(ids: List<AccountMessageId>): Either<Failure, Unit> =
        Either.catch { jpaAccountMessageRepository.deleteAllById(ids.map { id -> id.uuid }) }
            .mapLeft { Failure.DatabaseException(it) }
}
