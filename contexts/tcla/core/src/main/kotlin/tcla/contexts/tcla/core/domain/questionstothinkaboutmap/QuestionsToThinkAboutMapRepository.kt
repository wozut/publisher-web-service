package tcla.contexts.tcla.core.domain.questionstothinkaboutmap

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.questionstothinkaboutmap.model.QuestionsToThinkAboutMap
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.questionsToThinkAboutMap

@Named
class QuestionsToThinkAboutMapRepository {
    fun search(): List<QuestionsToThinkAboutMap> = listOf(questionsToThinkAboutMap)

}
