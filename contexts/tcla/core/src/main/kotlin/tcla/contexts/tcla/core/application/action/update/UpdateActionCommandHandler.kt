package tcla.contexts.tcla.core.application.action.update

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.ActionFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.action.ActionRepository
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.core.domain.action.rule.RequesterOwnsAction
import tcla.contexts.tcla.core.domain.questiontothinkabout.QuestionToThinkAboutRepository
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout
import tcla.contexts.tcla.core.domain.tcldriver.TclDriverRepository
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid
import java.util.*
import kotlin.collections.HashMap

@Named
class UpdateActionCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val actionRepository: ActionRepository,
    private val requesterOwnsAction: RequesterOwnsAction,
    private val tclDriverRepository: TclDriverRepository,
    private val questionToThinkAboutRepository: QuestionToThinkAboutRepository,
) {
    fun execute(command: UpdateActionCommand): Either<NonEmptyList<Failure>, UpdateActionSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { _ -> toUuid(command) }
                .flatMap { uuid -> actionRepository.find(Action.ActionId(uuid)) }
                .flatMap { action -> requesterOwnsAction.ensure(action) }
                .flatMap { action -> updateAction(action, command.fields) }
                .flatMap { updatedAction -> actionRepository.save(updatedAction) }
                .flatMap { UpdateActionSuccess.right() }
        }

    private fun toUuid(command: UpdateActionCommand): Either<NonEmptyList<Failure.StringIsNotUuid.ActionId>, UUID> =
        command.id.toUuid()
            .mapLeft { nonEmptyListOf(Failure.StringIsNotUuid.ActionId) }


    private fun updateAction(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> =
        updateTitle(action, fieldsToUpdate)
            .flatMap { updatedAction -> updateDescription(updatedAction, fieldsToUpdate) }
            .flatMap { updatedAction -> updateIsArchived(updatedAction, fieldsToUpdate) }
            .flatMap { updatedAction -> updateContext(updatedAction, fieldsToUpdate) }
            .flatMap { updatedAction -> updateChallenges(updatedAction, fieldsToUpdate) }
            .flatMap { updatedAction -> updateGoals(updatedAction, fieldsToUpdate) }
            .flatMap { updatedAction -> updateTargetTclDrivers(updatedAction, fieldsToUpdate) }
            .flatMap { updatedAction -> updateTargetQuestionsToThinkAbout(updatedAction, fieldsToUpdate) }


    private fun updateTargetTclDrivers(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "targetTclDrivers"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> ActionFailure.InvalidTargetTclDrivers.nel().left()
                else -> when (newValue) {
                    is List<*> -> newValue.mapOrAccumulate { element ->
                        when (element) {
                            is String -> element.right()
                            else -> ActionFailure.InvalidTargetTclDrivers.left()
                        }.flatMap {
                            it.toUuid().mapLeft { ActionFailure.InvalidTargetTclDrivers }
                        }.flatMap { TclDriverId(it).right() }.bind()
                    }.flatMap { tclDriverIds: List<TclDriverId> -> tclDriverRepository.findAllById(tclDriverIds) }
                        .flatMap { tclDrivers -> action.updateTargetTclDrivers(tclDrivers.toSet()).right() }

                    else -> ActionFailure.InvalidTargetTclDrivers.nel().left()
                }
            }

            false -> action.right()
        }
    }

    private fun updateTargetQuestionsToThinkAbout(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "targetQuestionsToThinkAbout"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> ActionFailure.InvalidTargetQuestionsToThinkAbout.nel().left()
                else -> when (newValue) {
                    is List<*> -> newValue.mapOrAccumulate { element ->
                        when (element) {
                            is String -> element.right()
                            else -> ActionFailure.InvalidTargetQuestionsToThinkAbout.left()
                        }.flatMap {
                            it.toUuid().mapLeft { ActionFailure.InvalidTargetQuestionsToThinkAbout }
                        }.flatMap { QuestionToThinkAbout.Id(it).right() }.bind()
                    }.flatMap { questionToThinkAboutIds: List<QuestionToThinkAbout.Id> ->
                        questionToThinkAboutRepository.findAllById(questionToThinkAboutIds)
                    }
                        .flatMap { questionToThinkAboutList ->
                            action.updateTargetQuestionsToThinkAbout(
                                questionToThinkAboutList.toSet()
                            ).right()
                        }

                    else -> ActionFailure.InvalidTargetQuestionsToThinkAbout.nel().left()
                }
            }

            false -> action.right()
        }
    }

    private fun updateTitle(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "title"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> ActionFailure.InvalidTitle.nel().left()
                else -> when (newValue) {
                    is String -> action.updateTitle(newValue).right()
                    else -> ActionFailure.InvalidTitle.nel().left()
                }
            }

            false -> action.right()
        }
    }

    private fun updateDescription(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "description"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> ActionFailure.InvalidDescription.nel().left()
                else -> when (newValue) {
                    is String -> action.updateDescription(newValue).right()
                    else -> ActionFailure.InvalidDescription.nel().left()
                }
            }

            false -> action.right()
        }
    }

    private fun updateIsArchived(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "isArchived"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> ActionFailure.InvalidIsArchived.nel().left()
                else -> when (newValue) {
                    is Boolean -> action.updateIsArchived(newValue).right()
                    else -> ActionFailure.InvalidIsArchived.nel().left()
                }
            }

            false -> action.right()
        }
    }

    private fun updateContext(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "context"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> action.updateContext(null).right()
                else -> when (newValue) {
                    is String -> action.updateContext(newValue).right()
                    else -> ActionFailure.InvalidContext.nel().left()
                }
            }

            false -> action.right()
        }
    }

    private fun updateChallenges(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "challenges"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> action.updateChallenges(null).right()
                else -> when (newValue) {
                    is String -> action.updateChallenges(newValue).right()
                    else -> ActionFailure.InvalidChallenges.nel().left()
                }
            }

            false -> action.right()
        }
    }

    private fun updateGoals(
        action: Action,
        fieldsToUpdate: HashMap<String, Any?>
    ): Either<NonEmptyList<Failure>, Action> {
        val key = "goals"
        return when (fieldsToUpdate.containsKey(key)) {
            true -> when (val newValue = fieldsToUpdate[key]) {
                null -> action.updateGoals(null).right()
                else -> when (newValue) {
                    is String -> action.updateGoals(newValue).right()
                    else -> ActionFailure.InvalidGoals.nel().left()
                }
            }

            false -> action.right()
        }
    }

}
