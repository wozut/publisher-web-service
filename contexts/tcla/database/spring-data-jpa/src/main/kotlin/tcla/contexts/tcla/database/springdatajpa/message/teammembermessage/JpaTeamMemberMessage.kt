package tcla.contexts.tcla.database.springdatajpa.message.teammembermessage

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
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessageId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.contexts.tcla.core.domain.teammember.model.TeamMemberId
import tcla.contexts.tcla.database.springdatajpa.message.toDomainChannel
import tcla.contexts.tcla.database.springdatajpa.message.toJpa
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaire
import tcla.contexts.tcla.database.springdatajpa.respondent.JpaRespondent
import tcla.contexts.tcla.database.springdatajpa.teammember.JpaTeamMember
import java.sql.Timestamp
import java.time.Instant
import java.util.Hashtable
import java.util.UUID


@Entity(name = "team_member_message")
@Table(name = "team_member_message", schema = "tcla")
data class JpaTeamMemberMessage(
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
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "team_member_id", foreignKey = ForeignKey(name = "fk_tcla_team_member"))
    val teamMember: JpaTeamMember,
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "respondent_id", foreignKey = ForeignKey(name = "fk_tcla_respondent"))
    val respondent: JpaRespondent?,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id", foreignKey = ForeignKey(name = "fk_tcla_survey"))
    val questionnaire: JpaQuestionnaire
) {
    fun toDomain(): Either<NonEmptyList<Failure>, TeamMemberMessage> =
        channel.toDomainChannel()
            .mapLeft { Failure.UnableToTransformIntoDomainData.TeamMemberMessage.nel() }
            .flatMap { channelAsDomain ->
                typeToDomain()
                    .flatMap { typeAsDomain ->
                        Either.catch { gson.fromJson<Hashtable<String, String>>(extraData, hashtableGsonType) }
                            .mapLeft { Failure.UnableToTransformIntoDomainData.TeamMemberMessage.nel() }
                            .flatMap { extraData ->
                                TeamMemberMessage(
                                    id = TeamMemberMessageId(id),
                                    channel = channelAsDomain,
                                    type = typeAsDomain,
                                    scheduledToBeSentAt = scheduledToBeSentAt.toInstant(),
                                    actuallySentAt = actuallySentAt?.toInstant(),
                                    teamMemberId = TeamMemberId(teamMember.id),
                                    respondentId = when(respondent) {
                                        null -> null
                                        else -> RespondentId(respondent.id)
                                    },
                                    surveyId = QuestionnaireId(questionnaire.id),
                                    extraData = extraData
                                ).right()
                            }
                    }
            }

    private fun typeToDomain(): Either<NonEmptyList<Failure>, TeamMemberMessage.Type> =
        when (type) {
            surveyInvitationString -> TeamMemberMessage.Type.SURVEY_INVITATION.right()
            _48HoursBeforeSurveyEndString -> TeamMemberMessage.Type._48_HOURS_BEFORE_SURVEY_END.right()
            surveyDurationExtendedString -> TeamMemberMessage.Type.SURVEY_DURATION_EXTENDED.right()
            else -> Failure.UnableToTransformIntoDomainData.TeamMemberMessage.nel().left()
        }
}
private val gson = Gson()
private val hashtableGsonType: java.lang.reflect.Type = object : TypeToken<Hashtable<String, String>>() {}.type
private const val surveyInvitationString = "survey-invitation"
private const val _48HoursBeforeSurveyEndString = "48-hours-before-survey-end"
private const val surveyDurationExtendedString = "survey-duration-extended"

fun TeamMemberMessage.toJpa(jpaTeamMember: JpaTeamMember, jpaRespondent: JpaRespondent?, jpaQuestionnaire: JpaQuestionnaire): Either<NonEmptyList<Failure>, JpaTeamMemberMessage> =
    Either.catch { gson.toJson(extraData) }
        .mapLeft { Failure.UnableToTransformIntoPersistenceData.TeamMemberMessage.nel() }
        .flatMap { extraData ->
            JpaTeamMemberMessage(
                id = id.uuid,
                channel = channel.toJpa(),
                type = when (type) {
                    TeamMemberMessage.Type.SURVEY_INVITATION -> surveyInvitationString
                    TeamMemberMessage.Type._48_HOURS_BEFORE_SURVEY_END -> _48HoursBeforeSurveyEndString
                    TeamMemberMessage.Type.SURVEY_DURATION_EXTENDED -> surveyDurationExtendedString
                },
                scheduledToBeSentAt = Timestamp(scheduledToBeSentAt.toEpochMilli()),
                actuallySentAt = actuallySentAt?.let { instant: Instant -> Timestamp(instant.toEpochMilli()) },
                teamMember = jpaTeamMember,
                respondent = jpaRespondent,
                questionnaire = jpaQuestionnaire,
                extraData = extraData
            ).right()
        }
