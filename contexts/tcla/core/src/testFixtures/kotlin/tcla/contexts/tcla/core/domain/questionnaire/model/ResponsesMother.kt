package tcla.contexts.tcla.core.domain.questionnaire.model

import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling

object ResponsesMother {
    fun default(
        maximumAmountToBeCollected: Responses.MaximumAmountToBeCollected = Responses.MaximumAmountToBeCollected(1),
        minimumRateRequired: Responses.MinimumRateRequired = Responses.MinimumRateRequired(1.0),
        collection: Set<QuestionnaireFilling> = setOf()
    ): Responses = Responses(
        maximumAmountToBeCollected = maximumAmountToBeCollected,
        minimumRateRequired = minimumRateRequired,
        collection = collection
    )
}
