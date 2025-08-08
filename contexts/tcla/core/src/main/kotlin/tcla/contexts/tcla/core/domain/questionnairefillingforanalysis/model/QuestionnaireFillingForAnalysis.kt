package tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model

import tcla.contexts.tcla.core.domain.answerforanalysis.model.AnswerForAnalysis

data class QuestionnaireFillingForAnalysis(
    val id: QuestionnaireFillingForAnalysisId,
    val answers: List<AnswerForAnalysis>
)
