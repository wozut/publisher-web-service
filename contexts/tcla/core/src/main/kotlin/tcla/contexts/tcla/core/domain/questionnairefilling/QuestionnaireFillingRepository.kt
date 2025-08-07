package tcla.contexts.tcla.core.domain.questionnairefilling

import arrow.core.Either
import tcla.contexts.tcla.core.application.response.search.SearchResponsesFailure
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.libraries.search.Filter

interface QuestionnaireFillingRepository {
    fun search(filter: Filter<ResponseFilterKey>): Either<SearchResponsesFailure, List<QuestionnaireFilling>>
}
