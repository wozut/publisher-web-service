package tcla.contexts.tcla.core.domain.tcldriverscore

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreId
import tcla.libraries.search.Filter

interface TclDriverScoreRepository {
    fun noneExists(tclDriverScoreIds: List<TclDriverScoreId>): Either<Failure, Boolean>
    fun saveAll(tclDriverScores: List<TclDriverScore>): Either<NonEmptyList<Failure>, SaveAllTclDriverScoresSuccess>
    fun search(filter: Filter<TclDriverScoreFilterKey>? = null): Either<NonEmptyList<Failure>, List<TclDriverScore>>
    fun deleteByAssessmentId(id: AssessmentId): Either<NonEmptyList<Failure>, Unit>
}
