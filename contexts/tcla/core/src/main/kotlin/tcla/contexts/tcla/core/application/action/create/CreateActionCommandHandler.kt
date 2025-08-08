package tcla.contexts.tcla.core.application.action.create

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.action.ActionRepository
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.rule.RequesterOwnsAssessmentRule
import tcla.contexts.tcla.core.domain.questiontothinkabout.QuestionToThinkAboutRepository
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout
import tcla.contexts.tcla.core.domain.tcldriver.TclDriverRepository
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class CreateActionCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule,
    private val requesterOwnsAssessmentRule: RequesterOwnsAssessmentRule,
    private val actionRepository: ActionRepository,
    private val questionToThinkAboutRepository: QuestionToThinkAboutRepository,
    private val tclDriverRepository: TclDriverRepository
) {
    fun execute(command: CreateActionCommand): Either<NonEmptyList<Failure>, CreateActionSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            requestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    command.assessmentId.toAssessmentId()
                        .flatMap { assessmentId ->
                            requesterOwnsAssessmentRule.ensure(assessmentId, requesterId)
                                .flatMap { ensureActionWithGivenIdDoesNotExist(Action.ActionId(UUID.randomUUID())) }
                                .flatMap { actionId -> buildAction(actionId, assessmentId, command) }
                        }
                }.flatMap { action -> actionRepository.save(action) }
                .flatMap { action -> CreateActionSuccess(action.id).right() }
        }

    private fun buildAction(
        actionId: Action.ActionId,
        assessmentId: AssessmentId,
        command: CreateActionCommand
    ): Either<NonEmptyList<Failure>, Action> =
        findQuestionsToThinkAbout(command.targetQuestionsToThinkAbout)
            .flatMap { questionsToThinkAbout ->
                findTclDrivers(command.targetTclDrivers)
                    .flatMap { tclDrivers ->
                        Action(
                            id = actionId,
                            assessmentId = assessmentId,
                            title = Action.Title(command.title),
                            description = Action.Description(command.description),
                            context = command.context?.let { Action.Context(it) },
                            challenges = command.challenges?.let { Action.Challenges(it) },
                            goals = command.goals?.let { Action.Goals(it) },
                            isArchived = false,
                            targetQuestionsToThinkAbout = Action.TargetQuestionsToThinkAbout(questionsToThinkAbout),
                            targetTclDrivers = Action.TargetTclDrivers(tclDrivers)
                        ).right()
                    }
            }

    private fun findQuestionsToThinkAbout(targetQuestionsToThinkAbout: List<String>): Either<NonEmptyList<Failure>, Set<QuestionToThinkAbout>> =
        targetQuestionsToThinkAbout.mapOrAccumulate {
            it.toUuid()
                .mapLeft { Failure.StringIsNotUuid.QuestionToThinkAboutId }
                .flatMap { uuid -> QuestionToThinkAbout.Id(uuid).right() }.bind()
        }.flatMap { ids: List<QuestionToThinkAbout.Id> -> questionToThinkAboutRepository.findAllById(ids) }
            .flatMap { it.toSet().right() }

    private fun findTclDrivers(targetTclDrivers: List<String>): Either<NonEmptyList<Failure>, Set<TclDriver>> =
        targetTclDrivers.mapOrAccumulate {
            it.toUuid()
                .mapLeft { Failure.StringIsNotUuid.TclDriverId }
                .flatMap { uuid -> TclDriverId(uuid).right() }.bind()
        }.flatMap { ids: List<TclDriverId> -> tclDriverRepository.findAllById(ids) }
            .flatMap { it.toSet().right() }

    private fun String.toAssessmentId(): Either<NonEmptyList<Failure>, AssessmentId> =
        toUuid()
            .mapLeft { nonEmptyListOf(Failure.StringIsNotUuid.AssessmentId) }
            .flatMap { AssessmentId(it).right() }

    private fun ensureActionWithGivenIdDoesNotExist(id: Action.ActionId): Either<NonEmptyList<Failure>, Action.ActionId> =
        actionRepository.exists(id)
            .flatMap { exists ->
                when (exists) {
                    true -> nonEmptyListOf(Failure.EntityAlreadyExists.Action).left()
                    false -> id.right()
                }
            }
}
