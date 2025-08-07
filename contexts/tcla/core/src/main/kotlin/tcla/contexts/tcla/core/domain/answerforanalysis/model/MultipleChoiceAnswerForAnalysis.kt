package tcla.contexts.tcla.core.domain.answerforanalysis.model

import tcla.contexts.tcla.core.domain.answeroptionforanalysis.model.AnswerOptionForAnalysis

class MultipleChoiceAnswerForAnalysis(
    id: AnswerForAnalysisId,
    val answerOption: AnswerOptionForAnalysis
) : AnswerForAnalysis(id)
