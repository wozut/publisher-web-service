package tcla.contexts.tcla.core.domain.runtclmodel

import arrow.core.Either
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysis

interface RunTclModel {
    fun execute(questionnaireFillingForAnalysisList: List<QuestionnaireFillingForAnalysis>, assessmentId: AssessmentId): Either<RunTclModelFailure, RunTclModelSuccess>
}
