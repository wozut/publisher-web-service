package tcla.contexts.tcla.core.application.response.search

import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling

data class SearchResponsesSuccess(val responses: List<QuestionnaireFilling>)
