package tcla.contexts.tcla.core.domain.questionnairefilling.model

import tcla.contexts.tcla.core.domain.answer.model.Answer

data class QuestionnaireFilling(
    val id: QuestionnaireFillingId,
    val answers: Set<Answer>,
    val externalId: ExternalId
)
