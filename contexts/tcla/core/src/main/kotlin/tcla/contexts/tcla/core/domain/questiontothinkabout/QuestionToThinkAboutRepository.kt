package tcla.contexts.tcla.core.domain.questiontothinkabout

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout

interface QuestionToThinkAboutRepository {

    fun findAllById(ids: List<QuestionToThinkAbout.Id>): Either<NonEmptyList<Failure>, List<QuestionToThinkAbout>>
}
