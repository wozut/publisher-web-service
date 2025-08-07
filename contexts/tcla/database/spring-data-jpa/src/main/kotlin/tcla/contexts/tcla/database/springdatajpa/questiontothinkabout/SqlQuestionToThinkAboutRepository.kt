package tcla.contexts.tcla.database.springdatajpa.questiontothinkabout

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questiontothinkabout.QuestionToThinkAboutRepository
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.questionsToThinkAboutMap

@Named
class SqlQuestionToThinkAboutRepository : QuestionToThinkAboutRepository {
    override fun findAllById(ids: List<QuestionToThinkAbout.Id>): Either<NonEmptyList<Failure>, List<QuestionToThinkAbout>> =
        questionsToThinkAboutMap.value.values
            .flatten()
            .filter { questionToThinkAbout -> ids.contains(questionToThinkAbout.id) }
            .let { matchingEntities: List<QuestionToThinkAbout> ->
                when (matchingEntities.size < ids.size) {
                    true -> Failure.EntityNotFound.QuestionToThinkAbout.nel().left()
                    false -> matchingEntities.right()
                }
            }


}
