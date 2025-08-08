package tcla.contexts.tcla.core.domain.questionnaire.model

sealed class ResponsesFailure {
    data object CollectionSizeExceedsMaximumAmountToBeCollected : ResponsesFailure()
}
