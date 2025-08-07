package tcla.contexts.tcla.database.springdatajpa.question

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.question.QuestionRepository
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.question.model.QuestionId

@Named
class SqlQuestionRepository(
    private val jpaQuestionRepository: JpaQuestionRepository
) : QuestionRepository {
    override fun noneExists(questionIds: List<QuestionId>): Either<Failure, Boolean> =
        questionIds.map { questionId -> questionId.uuid }
            .let { ids ->
                Either.catch { jpaQuestionRepository.findAllById(ids) }
                    .mapLeft(Failure::DatabaseException)
                    .flatMap { jpaQuestions -> jpaQuestions.isEmpty().right() }
            }

    override fun find(questionId: QuestionId): Either<Failure, Question> =
        Either.catch { jpaQuestionRepository.findByIdOrNull(questionId.uuid) }
            .mapLeft(Failure::DatabaseException)
            .flatMap { jpaQuestion: JpaQuestion? ->
                when(jpaQuestion) {
                    null -> Failure.EntityNotFound.Question.left()
                    else -> jpaQuestion.toDomain()
                }
            }
}
