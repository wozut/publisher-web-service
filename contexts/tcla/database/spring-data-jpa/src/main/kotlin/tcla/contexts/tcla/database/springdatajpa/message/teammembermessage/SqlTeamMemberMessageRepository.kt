package tcla.contexts.tcla.database.springdatajpa.message.teammembermessage

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
import tcla.contexts.tcla.core.domain.message.teammembermessage.TeamMemberMessageRepository
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaire
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaireRepository
import tcla.contexts.tcla.database.springdatajpa.respondent.JpaRespondent
import tcla.contexts.tcla.database.springdatajpa.respondent.JpaRespondentRepository
import tcla.contexts.tcla.database.springdatajpa.teammember.JpaTeamMember
import tcla.contexts.tcla.database.springdatajpa.teammember.JpaTeamMemberRepository
import java.sql.Timestamp
import java.time.Instant

@Named
class SqlTeamMemberMessageRepository(
    private val jpaTeamMemberMessageRepository: JpaTeamMemberMessageRepository,
    private val jpaTeamMemberRepository: JpaTeamMemberRepository,
    private val jpaQuestionnaireRepository: JpaQuestionnaireRepository,
    private val jpaRespondentRepository: JpaRespondentRepository
) : TeamMemberMessageRepository {
    override fun find(teamMemberMessageId: TeamMemberMessageId): Either<NonEmptyList<Failure>, TeamMemberMessage> =
        Either.catch { jpaTeamMemberMessageRepository.findByIdOrNull(teamMemberMessageId.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaTeamMemberMessage: JpaTeamMemberMessage? ->
                when (jpaTeamMemberMessage) {
                    null -> Failure.EntityNotFound.TeamMemberMessage.nel().left()
                    else -> jpaTeamMemberMessage.toDomain()
                }
            }

    override fun saveAll(teamMemberMessages: List<TeamMemberMessage>): Either<NonEmptyList<Failure>, Unit> =
        teamMemberMessages.mapOrAccumulate { teamMemberMessage: TeamMemberMessage ->
            Either.catch { jpaTeamMemberRepository.findByIdOrNull(teamMemberMessage.teamMemberId.uuid) }
                .mapLeft(Failure::DatabaseException)
                .flatMap { jpaTeamMember: JpaTeamMember? ->
                    when (jpaTeamMember) {
                        null -> Failure.EntityNotFound.TeamMember.left()
                        else -> jpaTeamMember.right()
                    }
                }.flatMap { jpaTeamMember: JpaTeamMember ->
                    when(val respondentId = teamMemberMessage.respondentId) {
                        null -> null.right()
                        else -> Either.catch { jpaRespondentRepository.findByIdOrNull(respondentId.uuid) }
                            .mapLeft(Failure::DatabaseException)
                            .flatMap { jpaRespondent: JpaRespondent? ->
                                when (jpaRespondent) {
                                    null -> Failure.EntityNotFound.Respondent.left()
                                    else -> jpaRespondent.right()
                                }
                            }
                    }
                    .flatMap { jpaRespondent: JpaRespondent? ->
                            Either.catch { jpaQuestionnaireRepository.findByIdOrNull(teamMemberMessage.surveyId.uuid) }
                                .mapLeft(Failure::DatabaseException)
                                .flatMap { jpaQuestionnaire: JpaQuestionnaire? ->
                                    when (jpaQuestionnaire) {
                                        null -> Failure.EntityNotFound.Survey.left()
                                        else -> jpaQuestionnaire.right()
                                    }
                                }.flatMap { jpaQuestionnaire: JpaQuestionnaire ->
                                    teamMemberMessage.toJpa(
                                        jpaTeamMember = jpaTeamMember,
                                        jpaRespondent = jpaRespondent,
                                        jpaQuestionnaire = jpaQuestionnaire
                                    ).mapLeft { Failure.UnableToTransformIntoPersistenceData.TeamMemberMessage }
                                }
                        }
                }.bind()
        }.flatMap { jpaTeamMemberMessages: List<JpaTeamMemberMessage> ->
            Either.catch { jpaTeamMemberMessageRepository.saveAll(jpaTeamMemberMessages) }
                .mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
        }.flatMap { Unit.right() }

    override fun save(teamMemberMessage: TeamMemberMessage): Either<NonEmptyList<Failure>, TeamMemberMessage> =
        Either.catch { jpaTeamMemberRepository.findByIdOrNull(teamMemberMessage.teamMemberId.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaTeamMember: JpaTeamMember? ->
                when (jpaTeamMember) {
                    null -> Failure.EntityNotFound.TeamMember.nel().left()
                    else -> jpaTeamMember.right()
                }
            }.flatMap { jpaTeamMember: JpaTeamMember ->
                when(val respondentId = teamMemberMessage.respondentId) {
                    null -> null.right()
                    else -> Either.catch { jpaRespondentRepository.findByIdOrNull(respondentId.uuid) }
                        .mapLeft { Failure.DatabaseException(it).nel() }
                        .flatMap { jpaRespondent: JpaRespondent? ->
                            when (jpaRespondent) {
                                null -> Failure.EntityNotFound.Respondent.nel().left()
                                else -> jpaRespondent.right()
                            }
                        }
                }
                .flatMap { jpaRespondent: JpaRespondent? ->
                        Either.catch { jpaQuestionnaireRepository.findByIdOrNull(teamMemberMessage.surveyId.uuid) }
                            .mapLeft { Failure.DatabaseException(it).nel() }
                            .flatMap { jpaQuestionnaire: JpaQuestionnaire? ->
                                when (jpaQuestionnaire) {
                                    null -> Failure.EntityNotFound.Questionnaire.nel().left()
                                    else -> jpaQuestionnaire.right()
                                }
                            }.flatMap { jpaQuestionnaire: JpaQuestionnaire ->
                                teamMemberMessage.toJpa(
                                    jpaTeamMember = jpaTeamMember,
                                    jpaRespondent = jpaRespondent,
                                    jpaQuestionnaire = jpaQuestionnaire
                                )
                            }
                    }
            }.flatMap { jpaTeamMemberMessage: JpaTeamMemberMessage ->
                Either.catch { jpaTeamMemberMessageRepository.save(jpaTeamMemberMessage) }
                    .mapLeft { Failure.DatabaseException(it).nel() }
            }.flatMap { jpaTeamMemberMessage: JpaTeamMemberMessage ->
                jpaTeamMemberMessage.toDomain()
            }

    override fun searchByActuallySentAtIsNullAndScheduledToBeSentAtBefore(instant: Instant): Either<NonEmptyList<Failure>, List<TeamMemberMessage>> =
        Either.catch {
            jpaTeamMemberMessageRepository.findAllByActuallySentAtIsNullAndScheduledToBeSentAtBefore(
                Timestamp(instant.toEpochMilli())
            )
        }.mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
            .flatMap { jpaTeamMemberMessages: List<JpaTeamMemberMessage> ->
                jpaTeamMemberMessages.mapOrAccumulate { jpaTeamMemberMessage: JpaTeamMemberMessage ->
                    jpaTeamMemberMessage.toDomain().bindNel()
                }
            }

    override fun searchBySurveyIdAndActuallySentAtIsNull(surveyId: QuestionnaireId): Either<NonEmptyList<Failure>, List<TeamMemberMessage>> =
        Either.catch { jpaTeamMemberMessageRepository.findAllByQuestionnaire_IdAndActuallySentAtIsNull(surveyId.uuid) }
            .mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
            .flatMap { jpaTeamMemberMessages ->
                jpaTeamMemberMessages.mapOrAccumulate { jpaTeamMemberMessage ->
                    jpaTeamMemberMessage.toDomain()
                        .bindNel()
                }
            }

    override fun deleteAll(ids: List<TeamMemberMessageId>): Either<Failure, Unit> =
        Either.catch { jpaTeamMemberMessageRepository.deleteAllById(ids.map { id -> id.uuid }) }
            .mapLeft { Failure.DatabaseException(it) }
}
