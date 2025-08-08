package tcla.contexts.tcla.core.application.questionstothinkaboutmap.search

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.questionstothinkaboutmap.QuestionsToThinkAboutMapRepository

@Named
class SearchQuestionsToThinkAboutMapsQueryHandler(
    private val questionsToThinkAboutMapRepository: QuestionsToThinkAboutMapRepository
) {
    fun execute(): SearchQuestionsToThinkAboutMapsSuccess =
        SearchQuestionsToThinkAboutMapsSuccess(questionsToThinkAboutMapRepository.search())

}

