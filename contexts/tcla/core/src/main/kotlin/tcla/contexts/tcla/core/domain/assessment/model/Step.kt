package tcla.contexts.tcla.core.domain.assessment.model

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import tcla.contexts.tcla.core.application.failures.StepFailure

sealed interface Step {
    val successors: Set<Step>

    fun stepForwardTo(step: Step): Either<StepFailure, Step> =
        either {
            ensure(this@Step.successors.contains(step)) { StepFailure.InvalidStep }
            step
        }

    fun stepBackwardTo(step: Step): Either<NonEmptyList<StepFailure>, Step> =
        either {
            ensure(step.successors.contains(this@Step)) { StepFailure.InvalidStep.nel() }
            step
        }

    fun isCancelable(): Boolean = successors.contains(Canceled)

    companion object {
        fun stepsFromWhichCanCancel(): Set<Step> = allSteps().filter { step: Step -> step.isCancelable() }.toSet()

        fun allSteps(): Set<Step> = setOf(
            Scheduled,
            CollectingData,
            AnalysableData,
            ResultsAvailable,
            Canceled,
            DataCollected,
            DataNotAnalysable
        )
    }

    data object Scheduled: Step {
        override val successors: Set<Step> = setOf(Canceled, CollectingData)
    }
    data object CollectingData: Step {
        override val successors: Set<Step> = setOf(Canceled, DataCollected)
    }

    data object DataCollected: Step {
        override val successors: Set<Step> = setOf(AnalysableData, DataNotAnalysable)
    }

    data object AnalysableData: Step {
        override val successors: Set<Step> = setOf(ResultsAvailable)
    }

    data object DataNotAnalysable: Step {
        override val successors: Set<Step> = emptySet()
    }

    data object ResultsAvailable: Step {
        override val successors: Set<Step> = emptySet()
    }

    data object Canceled: Step {
        override val successors: Set<Step> = emptySet()
    }
}


