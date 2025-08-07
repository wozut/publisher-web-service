package tcla.contexts.tcla.core.domain.questionstothinkaboutmap.model

import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout

data class QuestionsToThinkAboutMap(
    val value: Map<Driver, List<QuestionToThinkAbout>>
)
