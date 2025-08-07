package tcla.contexts.tcla.core.domain.answeroptionforanalysis.model

import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestion

data class AnswerOptionForAnalysis(
    val id: AnswerOptionForAnalysisId,
    val value: ValueForAnalysis,
    val multipleChoiceQuestion: MultipleChoiceQuestion
)
