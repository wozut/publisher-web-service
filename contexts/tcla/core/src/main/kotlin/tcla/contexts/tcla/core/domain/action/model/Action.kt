package tcla.contexts.tcla.core.domain.action.model

import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import java.util.UUID

data class Action(
    val id: ActionId,
    val assessmentId: AssessmentId,
    val title: Title,
    val targetQuestionsToThinkAbout: TargetQuestionsToThinkAbout,
    val targetTclDrivers: TargetTclDrivers,
    val description: Description,
    val context: Context? = null,
    val challenges: Challenges? = null,
    val goals: Goals? = null,
    val isArchived: Boolean
) {
    fun updateTitle(newTitle: String) = copy(title = Title(newTitle))
    fun updateDescription(newDescription: String) = copy(description = Description(newDescription))
    fun updateIsArchived(newIsArchived: Boolean) = copy(isArchived = newIsArchived)
    fun updateContext(newContext: String?) = copy(context = newContext?.let { Context(it) })
    fun updateChallenges(newChallenges: String?) = copy(challenges = newChallenges?.let { Challenges(it) })
    fun updateGoals(newGoals: String?) = copy(goals = newGoals?.let { Goals(it) })

    fun updateTargetQuestionsToThinkAbout(newTargetQuestionsToThinkAbout: Set<QuestionToThinkAbout>) =
        copy(targetQuestionsToThinkAbout = TargetQuestionsToThinkAbout(newTargetQuestionsToThinkAbout))

    fun updateTargetTclDrivers(newTargetTclDrivers: Set<TclDriver>) =
        copy(targetTclDrivers = TargetTclDrivers(newTargetTclDrivers))


    @JvmInline
    value class ActionId(val value: UUID)
    @JvmInline
    value class Title(val value: String)
    @JvmInline
    value class TargetQuestionsToThinkAbout(val value: Set<QuestionToThinkAbout>)
    @JvmInline
    value class TargetTclDrivers(val value: Set<TclDriver>)
    @JvmInline
    value class Description(val value: String)
    @JvmInline
    value class Context(val value: String)
    @JvmInline
    value class Challenges(val value: String)
    @JvmInline
    value class Goals(val value: String)
}
