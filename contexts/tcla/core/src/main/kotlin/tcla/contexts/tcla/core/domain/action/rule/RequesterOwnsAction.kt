package tcla.contexts.tcla.core.domain.action.rule

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.core.domain.assessment.rule.RequesterOwnsAssessmentRule

@Named
class RequesterOwnsAction(
    private val requesterOwnsAssessmentRule: RequesterOwnsAssessmentRule
) {
    fun ensure(
        action: Action
    ): Either<NonEmptyList<Failure>, Action> =
        requesterOwnsAssessmentRule.ensure(action.assessmentId)
            .flatMap { action.right() }

}
