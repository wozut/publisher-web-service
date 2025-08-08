package tcla.contexts.tcla.database.springdatajpa.message.accountmessage

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.database.springdatajpa.message.toDomainChannel
import tcla.contexts.tcla.database.springdatajpa.message.toJpa
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaire
import java.sql.Timestamp
import java.time.Instant
import java.util.Hashtable
import java.util.UUID


@Entity(name = "account_message")
@Table(name = "account_message", schema = "tcla")
data class JpaAccountMessage(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val channel: String,
    @Column(nullable = false)
    val type: String,
    @Column(nullable = false)
    val scheduledToBeSentAt: Timestamp,
    @Column(nullable = true)
    val actuallySentAt: Timestamp?,
    @Column(nullable = false)
    val extraData: String,
    @Column(nullable = false)
    val accountId: String,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id", foreignKey = ForeignKey(name = "fk_tcla_survey"))
    val questionnaire: JpaQuestionnaire
) {
    fun toDomain(): Either<NonEmptyList<Failure>, AccountMessage> =
        channel.toDomainChannel()
            .mapLeft { Failure.UnableToTransformIntoDomainData.AccountMessage.nel() }
            .flatMap { channelAsDomain ->
                typeToDomain()
                    .flatMap { typeAsDomain ->
                        Either.catch { gson.fromJson<Hashtable<String, String>>(extraData, hashtableGsonType) }
                            .mapLeft { Failure.UnableToTransformIntoDomainData.AccountMessage.nel() }
                            .flatMap { extraData ->
                                AccountMessage(
                                    id = AccountMessageId(id),
                                    channel = channelAsDomain,
                                    type = typeAsDomain,
                                    scheduledToBeSentAt = scheduledToBeSentAt.toInstant(),
                                    actuallySentAt = actuallySentAt?.toInstant(),
                                    accountId = accountId,
                                    surveyId = QuestionnaireId(questionnaire.id),
                                    extraData = extraData
                                ).right()
                            }
                    }
            }

    private fun typeToDomain(): Either<NonEmptyList<Failure>, AccountMessage.Type> =
        when (type) {
            _48HoursBeforeSurveyEndString -> AccountMessage.Type._48_HOURS_BEFORE_SURVEY_END.right()
            resultsAvailable -> AccountMessage.Type.RESULTS_AVAILABLE.right()
            resultsNotAvailable -> AccountMessage.Type.RESULTS_NOT_AVAILABLE.right()
            canceledSurvey -> AccountMessage.Type.CANCELED_SURVEY.right()
            else -> Failure.UnableToTransformIntoDomainData.AccountMessage.nel().left()
        }
}

private val gson = Gson()
private val hashtableGsonType: java.lang.reflect.Type = object : TypeToken<Hashtable<String, String>>() {}.type
private const val _48HoursBeforeSurveyEndString = "48-hours-before-survey-end"
private const val resultsAvailable = "results-available"
private const val resultsNotAvailable = "results-not-available"
private const val canceledSurvey = "canceled-survey"

fun AccountMessage.toJpa(jpaQuestionnaire: JpaQuestionnaire): Either<AccountMessageToJpaFailure, JpaAccountMessage> =
    Either.catch { gson.toJson(extraData) }
        .mapLeft { AccountMessageToJpaFailure.NotSerializable }
        .flatMap { extraData ->
            JpaAccountMessage(
                id = id.uuid,
                channel = channel.toJpa(),
                type = when (type) {
                    AccountMessage.Type._48_HOURS_BEFORE_SURVEY_END -> _48HoursBeforeSurveyEndString
                    AccountMessage.Type.RESULTS_AVAILABLE -> resultsAvailable
                    AccountMessage.Type.RESULTS_NOT_AVAILABLE -> resultsNotAvailable
                    AccountMessage.Type.CANCELED_SURVEY -> canceledSurvey
                },
                scheduledToBeSentAt = Timestamp(scheduledToBeSentAt.toEpochMilli()),
                actuallySentAt = actuallySentAt?.let { instant: Instant -> Timestamp(instant.toEpochMilli()) },
                accountId = accountId,
                questionnaire = jpaQuestionnaire,
                extraData = extraData
            ).right()
        }
