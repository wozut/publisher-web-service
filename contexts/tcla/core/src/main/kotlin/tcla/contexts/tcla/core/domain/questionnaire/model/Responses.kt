package tcla.contexts.tcla.core.domain.questionnaire.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import kotlin.math.ceil
import kotlin.math.max

private const val MINIMUM_RESPONDENTS: Int = 3

data class Responses(
    val maximumAmountToBeCollected: MaximumAmountToBeCollected,
    val minimumRateRequired: MinimumRateRequired,
    val collection: Set<QuestionnaireFilling>
) {
    val currentAmountCollected: Int = collection.size
    val minimumAmountRequired: Int =
        max(ceil(maximumAmountToBeCollected.int.toDouble() * minimumRateRequired.double).toInt(), MINIMUM_RESPONDENTS)
    val currentRateCollected: Double = collection.size.toDouble() / maximumAmountToBeCollected.int.toDouble()

    fun add(questionnaireFilling: QuestionnaireFilling): Either<ResponsesFailure, Responses> {
        return copy(collection = this.collection.plus(questionnaireFilling))
            .ensureCollectionSizeDoNotExceedMaximumAmountToBeCollected()
    }

    private fun Responses.ensureCollectionSizeDoNotExceedMaximumAmountToBeCollected(): Either<ResponsesFailure, Responses> =
        when (this.collection.size <= this.maximumAmountToBeCollected.int) {
            true -> this.right()
            false -> ResponsesFailure.CollectionSizeExceedsMaximumAmountToBeCollected.left()
        }

    @JvmInline
    value class MaximumAmountToBeCollected(val int: Int)

    @JvmInline
    value class MinimumRateRequired(val double: Double)
}
