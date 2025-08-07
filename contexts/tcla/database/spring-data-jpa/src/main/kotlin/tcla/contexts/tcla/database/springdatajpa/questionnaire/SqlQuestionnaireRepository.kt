package tcla.contexts.tcla.database.springdatajpa.questionnaire

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flattenOrAccumulate
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.answer.model.MultipleChoiceAnswer
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestion
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireFilterKey
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.database.springdatajpa.answer.JpaAnswerRepository
import tcla.contexts.tcla.database.springdatajpa.answer.toJpa
import tcla.contexts.tcla.database.springdatajpa.answeroption.JpaAnswerOption
import tcla.contexts.tcla.database.springdatajpa.answeroption.JpaAnswerOptionRepository
import tcla.contexts.tcla.database.springdatajpa.answeroption.toJpa
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessment
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessmentRepository
import tcla.contexts.tcla.database.springdatajpa.assessment.toJpa
import tcla.contexts.tcla.database.springdatajpa.question.JpaMultipleChoiceQuestion
import tcla.contexts.tcla.database.springdatajpa.question.JpaQuestionRepository
import tcla.contexts.tcla.database.springdatajpa.question.toJpa
import tcla.contexts.tcla.database.springdatajpa.questionnairefilling.JpaQuestionnaireFillingRepository
import tcla.contexts.tcla.database.springdatajpa.questionnairefilling.toJpa
import tcla.contexts.tcla.database.springdatajpa.toTimestamp
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import java.time.Instant

@Named
class SqlQuestionnaireRepository(
    private val jpaQuestionnaireRepository: JpaQuestionnaireRepository,
    private val jpaAssessmentRepository: JpaAssessmentRepository,
    private val jpaQuestionRepository: JpaQuestionRepository,
    private val jpaAnswerOptionRepository: JpaAnswerOptionRepository,
    private val jpaQuestionnaireFillingRepository: JpaQuestionnaireFillingRepository,
    private val jpaAnswerRepository: JpaAnswerRepository
) : QuestionnaireRepository {
    override fun exists(id: QuestionnaireId): Either<Failure, Boolean> =
        Either.catch { jpaQuestionnaireRepository.existsById(id.uuid) }
            .mapLeft(Failure::DatabaseException)
            .flatMap { exists: Boolean -> exists.right() }

    override fun find(id: QuestionnaireId): Either<NonEmptyList<Failure>, Questionnaire> =
        Either.catch { jpaQuestionnaireRepository.findByIdOrNull(id.uuid) }
            .mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(throwable))  }
            .flatMap { jpaQuestionnaire: JpaQuestionnaire? ->
                when (jpaQuestionnaire) {
                    null -> nonEmptyListOf(Failure.EntityNotFound.Questionnaire).left()
                    else -> jpaQuestionnaire.toDomain()
                }
            }.flatMap { questionnaire: Questionnaire -> questionnaire.right() }

    override fun save(questionnaire: Questionnaire): Either<NonEmptyList<Failure>, Questionnaire> =
        Either.catch { jpaAssessmentRepository.findByIdOrNull(questionnaire.assessmentId.uuid) }
            .mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
            .flatMap { jpaAssessment: JpaAssessment? ->
                when (jpaAssessment) {
                    null -> nonEmptyListOf(Failure.EntityNotFound.Assessment).left()
                    else -> questionnaire.toJpa(jpaAssessment).right()
                }
            }.flatMap { jpaQuestionnaire ->
                Either.catch { jpaQuestionnaireRepository.save(jpaQuestionnaire) }
                    .mapLeft { nonEmptyListOf(Failure.DatabaseException(it)) }
            }.flatMap { jpaQuestionnaire: JpaQuestionnaire ->
                saveQuestions(jpaQuestionnaire, questionnaire.questions)
                    .mapLeft { nonEmptyListOf(it) }
            }.flatMap { jpaQuestionnaire: JpaQuestionnaire ->
                saveQuestionnaireFillings(jpaQuestionnaire, questionnaire.responses.collection)
                    .mapLeft { nonEmptyListOf(it) }
            }.flatMap { jpaQuestionnaire ->
                jpaQuestionnaire.toDomain()
            }

    override fun search(filter: Filter<QuestionnaireFilterKey>?): Either<NonEmptyList<Failure>, List<Questionnaire>> =
        findJpaQuestionnaires(filter)
            .mapLeft { nonEmptyListOf(it) }
            .flatMap { jpaQuestionnaires: List<JpaQuestionnaire> ->
                jpaQuestionnaires.mapOrAccumulate { jpaQuestionnaire -> jpaQuestionnaire.toDomain().bindNel() }
            }

    private fun findJpaQuestionnaires(filter: Filter<QuestionnaireFilterKey>?): Either<Failure, List<JpaQuestionnaire>> =
        when (filter) {
            null -> Either.catch { jpaQuestionnaireRepository.findAll() }
                .mapLeft { throwable -> Failure.DatabaseException(exception = throwable) }

            else -> when (filter) {
                is ManyValuesFilter<QuestionnaireFilterKey, *> -> Failure.UnsupportedDatabaseFilter.Survey.left()

                is OneValueFilter<QuestionnaireFilterKey, *> -> when (filter.key) {
                    QuestionnaireFilterKey.ASSESSMENT -> when (val value = filter.value) {
                        is AssessmentId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaQuestionnaireRepository.findAllByAssessment_Id(value.uuid)
                            }.mapLeft { throwable -> Failure.DatabaseException(throwable) }

                            Operator.NaryOperator.In -> Failure.UnsupportedDatabaseFilter.Survey.left()
                        }

                        else -> Failure.UnsupportedDatabaseFilter.Survey.left()
                    }

                    QuestionnaireFilterKey.TEAM -> when (val value = filter.value) {
                        is TeamId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaQuestionnaireRepository.findAllByAssessment_Team_Id(value.uuid)
                            }.mapLeft { throwable -> Failure.DatabaseException(throwable) }

                            Operator.NaryOperator.In -> Failure.UnsupportedDatabaseFilter.Survey.left()
                        }

                        else -> Failure.UnsupportedDatabaseFilter.Survey.left()
                    }
                }
            }
        }

    private fun saveQuestionnaireFillings(
        jpaQuestionnaire: JpaQuestionnaire,
        questionnaireFillings: Set<QuestionnaireFilling>
    ): Either<Failure, JpaQuestionnaire> =
        Either.catch {
            questionnaireFillings.map { questionnaireFilling ->
                questionnaireFilling.toJpa(jpaQuestionnaire)
                    .let { jpaQuestionnaireFilling -> jpaQuestionnaireFillingRepository.save(jpaQuestionnaireFilling) }
                    .let { jpaQuestionnaireFilling ->
                        questionnaireFilling.answers.map { answer ->
                            when (answer) {
                                is MultipleChoiceAnswer -> {
                                    val jpaAnswerOption: JpaAnswerOption? = jpaQuestionnaire
                                        .questions
                                        .filterIsInstance<JpaMultipleChoiceQuestion>()
                                        .map { jpaMultipleChoiceQuestion -> jpaMultipleChoiceQuestion.answerOptions }
                                        .flatten()
                                        .find { jpaAnswerOption -> jpaAnswerOption.id == answer.answerOption.id.value }
                                    when (jpaAnswerOption) {
                                        null -> TODO()
                                        else -> answer.toJpa(jpaQuestionnaireFilling, jpaAnswerOption)
                                    }
                                }
                            }
                        }.let { jpaMultipleChoiceAnswers ->
                            jpaAnswerRepository.saveAll(jpaMultipleChoiceAnswers)
                        }.let { jpaMultipleChoiceAnswers ->
                            when(jpaMultipleChoiceAnswers.isEmpty()) {
                                true -> jpaQuestionnaireFilling
                                false -> {
                                    val areAnswersAdded = jpaQuestionnaireFilling.answers.addAll(jpaMultipleChoiceAnswers)
                                    when (areAnswersAdded) {
                                        true -> jpaQuestionnaireFillingRepository.save(jpaQuestionnaireFilling)
                                        false -> TODO()
                                    }
                                }
                            }
                        }
                    }
            }.let { jpaQuestionnaireFillings ->
                when(jpaQuestionnaireFillings.isEmpty()) {
                    true -> jpaQuestionnaire
                    false -> {
                        val areQuestionnaireFillingsAdded = jpaQuestionnaire.questionnaireFillings.addAll(jpaQuestionnaireFillings)
                        when (areQuestionnaireFillingsAdded) {
                            true -> jpaQuestionnaireRepository.save(jpaQuestionnaire)
                            false -> TODO()
                        }
                    }
                }
            }
        }.mapLeft(Failure::DatabaseException)

    private fun saveQuestions(
        jpaQuestionnaire: JpaQuestionnaire,
        questions: Set<Question>
    ): Either<Failure, JpaQuestionnaire> =
        Either.catch {
            questions.map { question: Question ->
                when (question) {
                    is MultipleChoiceQuestion ->
                        question.toJpa(jpaQuestionnaire)
                            .let { jpaMultipleChoiceQuestion -> jpaQuestionRepository.save(jpaMultipleChoiceQuestion) }
                            .let { jpaQuestion ->
                                question.answerOptions.map { answerOption -> answerOption.toJpa(jpaQuestion) }
                                    .let { jpaAnswerOptions -> jpaAnswerOptionRepository.saveAll(jpaAnswerOptions) }
                                    .let { jpaAnswerOptions: MutableList<JpaAnswerOption> ->
                                        when(jpaAnswerOptions.isEmpty()) {
                                            true -> jpaQuestion
                                            false -> {
                                                val areAnswerOptionsAdded = jpaQuestion.answerOptions.addAll(jpaAnswerOptions)
                                                when (areAnswerOptionsAdded) {
                                                    true -> jpaQuestionRepository.save(jpaQuestion)
                                                    false -> TODO()
                                                }
                                            }
                                        }
                                    }
                            }
                }
            }.let { jpaQuestions: List<JpaMultipleChoiceQuestion> ->
                when(jpaQuestions.isEmpty()) {
                    true -> jpaQuestionnaire
                    else -> {
                        val areQuestionsAdded = jpaQuestionnaire.questions.addAll(jpaQuestions)
                        when (areQuestionsAdded) {
                            true -> jpaQuestionnaireRepository.save(jpaQuestionnaire)
                            false -> TODO()
                        }
                    }
                }
            }
        }.mapLeft(Failure::DatabaseException)


    override fun searchByExternalQuestionnaireIdIsNull(): Either<NonEmptyList<Failure>, List<Questionnaire>> =
        Either.catch { jpaQuestionnaireRepository.findAllByExternalQuestionnaireIdIsNull() }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaQuestionnaires: List<JpaQuestionnaire> ->
                jpaQuestionnaires.mapOrAccumulate { it.toDomain().bindNel() }
            }

    override fun searchByExternalQuestionnaireIdIsNotNullAndStartDateBeforeAndExternalQuestionnaireIsPublic(
        instant: Instant,
        externalQuestionnaireIsPublic: Boolean
    ): Either<NonEmptyList<Failure>, List<Questionnaire>> =
        instant.toTimestamp()
            .mapLeft { it.nel() }
            .flatMap { timestamp ->
                Either.catch {
                    jpaQuestionnaireRepository.findAllByExternalQuestionnaireIdIsNotNullAndStartDateBeforeAndExternalQuestionnaireIsPublic(
                        timestamp,
                        externalQuestionnaireIsPublic
                    )
                }.mapLeft { Failure.DatabaseException(it).nel() }
            }.flatMap { jpaQuestionnaires: List<JpaQuestionnaire> -> jpaQuestionnaires.toDomain() }

    override fun searchByAssessment_Team_IdAndAssessment_CurrentStepIsIn(
        teamId: TeamId,
        steps: Set<Step>
    ): Either<NonEmptyList<Failure>, List<Questionnaire>> =
        Either.catch { jpaQuestionnaireRepository.findAllByAssessment_Team_IdAndAssessment_CurrentStepIsIn(teamId.uuid, steps.map { it.toJpa() }) }
            .mapLeft { nonEmptyListOf(Failure.DatabaseException(it))  }
            .flatMap { surveys -> surveys.mapOrAccumulate { survey -> survey.toDomain().bindNel() } }

    override fun searchFirstByAssessment_Team_IdAndAssessment_CurrentStepIsNotOrderByEndDateDesc(
        teamId: TeamId,
        step: Step
    ): Either<NonEmptyList<Failure>, List<Questionnaire>> =
        Either.catch { jpaQuestionnaireRepository.findFirstByAssessment_Team_IdAndAssessment_CurrentStepIsNotOrderByEndDateDesc(teamId.uuid, step.toJpa()) }
            .mapLeft { throwable -> Failure.DatabaseException(throwable).nel() }
            .flatMap { jpaQuestionnaires: List<JpaQuestionnaire> ->
                jpaQuestionnaires
                    .map { jpaQuestionnaire -> jpaQuestionnaire.toDomain() }
                    .flattenOrAccumulate()
            }

    private fun List<JpaQuestionnaire>.toDomain(): Either<NonEmptyList<Failure>,List<Questionnaire>> = mapOrAccumulate { it.toDomain().bindNel() }
}
