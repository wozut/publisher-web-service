package tcla.contexts.tcla.core.application.respondent.search

import tcla.contexts.tcla.core.domain.respondent.model.Respondent

data class SearchRespondentsSuccess(val respondents: List<Respondent>)
