package tcla.contexts.tcla.core.domain.assessment.rule

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.CreateAssessmentFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.checkwhetherassessmentcanbecreated.CheckWhetherAssessmentCanBeCreated
import tcla.contexts.tcla.core.domain.team.model.TeamId

@Named
class AssessmentCanBeCreatedRule(
    private val checkWhetherAssessmentCanBeCreated: CheckWhetherAssessmentCanBeCreated
) {
    fun ensure(teamId: TeamId): Either<NonEmptyList<Failure>, Unit> =
        checkWhetherAssessmentCanBeCreated.execute(teamId)
            .flatMap { result ->
                when (result) {
                    true -> Unit.right()
                    false -> CreateAssessmentFailure.UnableToCreate.nel().left()
                }
            }

}
