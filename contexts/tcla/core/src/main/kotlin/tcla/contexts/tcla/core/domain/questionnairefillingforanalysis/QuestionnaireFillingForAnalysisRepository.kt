package tcla.contexts.tcla.core.domain.questionnairefillingforanalysis

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysis
import tcla.libraries.search.Filter

interface QuestionnaireFillingForAnalysisRepository {
    fun search(filter: Filter<QuestionnaireFillingForAnalysisFilterKey>? = null): Either<NonEmptyList<Failure>, List<QuestionnaireFillingForAnalysis>>
}
