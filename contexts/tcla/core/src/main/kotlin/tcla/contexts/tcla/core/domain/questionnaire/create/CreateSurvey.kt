package tcla.contexts.tcla.core.domain.questionnaire.create

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.question.QuestionRepository
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import tcla.contexts.tcla.core.domain.questionnaire.rule.NotOverlappingQuestionnairesByTeamRule
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.teammember.TeamMemberFilterKey
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.contexts.tcla.core.domain.teammember.model.TeamMember
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.util.UUID

@Named
class CreateSurvey(
    private val questionnaireRepository: QuestionnaireRepository,
    private val questionRepository: QuestionRepository,
    private val transactionExecutor: TransactionExecutor,
    private val notOverlappingQuestionnairesByTeamRule: NotOverlappingQuestionnairesByTeamRule,
    private val teamMemberRepository: TeamMemberRepository
) {
    fun execute(
        assessmentId: AssessmentId,
        interval: ResponseAcceptanceInterval,
        teamId: TeamId,
        includeGenderQuestion: Boolean,
        includeWorkFamiliarityQuestion: Boolean,
        includeTeamFamiliarityQuestion: Boolean,
        includeModeOfWorkingQuestion: Boolean
    ): Either<NonEmptyList<Failure>, Questionnaire> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            QuestionnaireId(UUID.randomUUID())
                .ensureNotExists()
                .flatMap { questionnaireId ->
                    notOverlappingQuestionnairesByTeamRule.ensure(questionnaireId, interval, teamId)
                        .flatMap { questionnaireId.right() }
                }.flatMap { questionnaireId ->
                    searchTeamMembers(teamId)
                        .flatMap { teamMembers ->
                            buildTclQuestionnaire(
                                questionnaireId = questionnaireId,
                                externalQuestionnaireId = null,
                                externalQuestionnaireIsPublic = false,
                                assessmentId = assessmentId,
                                interval = interval,
                                maximumAmountToBeCollected = teamMembers.size,
                                includeGenderQuestion = includeGenderQuestion,
                                includeWorkFamiliarityQuestion = includeWorkFamiliarityQuestion,
                                includeTeamFamiliarityQuestion = includeTeamFamiliarityQuestion,
                                includeModeOfWorkingQuestion = includeModeOfWorkingQuestion
                            ).right()
                        }
                }.flatMap { questionnaire -> questionnaire.ensureQuestionIdsAreUnique() }
                .flatMap { questionnaire -> questionnaire.ensureQuestionIdsNotYetExist() }
                .flatMap { questionnaire ->
                    save(questionnaire)
                        .flatMap { questionnaire.right() }
                }
        }

    private fun searchTeamMembers(teamId: TeamId): Either<NonEmptyList<Failure>, List<TeamMember>> =
        teamMemberRepository.search(OneValueFilter(TeamMemberFilterKey.TEAM, teamId))
            .mapLeft { nonEmptyListOf(it) }

    private fun Questionnaire.ensureQuestionIdsNotYetExist(): Either<NonEmptyList<Failure>, Questionnaire> =
        questionRepository.noneExists(this.questions.map(Question::id))
            .mapLeft { nonEmptyListOf(it) }
            .flatMap { noneExists ->
                when (noneExists) {
                    true -> this.right()
                    false -> nonEmptyListOf(Failure.EntityAlreadyExists.Question).left()
                }
            }

    private fun save(questionnaire: Questionnaire): Either<NonEmptyList<Failure>, Questionnaire> =
        questionnaireRepository.save(questionnaire)

    private fun QuestionnaireId.ensureNotExists(): Either<NonEmptyList<Failure>, QuestionnaireId> =
        questionnaireRepository.exists(this)
            .mapLeft { nonEmptyListOf(it) }
            .flatMap {
                when (it) {
                    true -> nonEmptyListOf(Failure.EntityAlreadyExists.Survey).left()
                    false -> this.right()
                }
            }


    private fun Questionnaire.ensureQuestionIdsAreUnique(): Either<NonEmptyList<Failure>, Questionnaire> {
        val questionIds = this.questions.map(Question::id)
        return when (questionIds.size == questionIds.distinct().size) {
            true -> this.right()
            false -> nonEmptyListOf(Failure.GeneratedIdsAreNotDistinct.Question).left()
        }
    }
}
