package tcla.contexts.tcla.database.springdatajpa.action

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flattenOrAccumulate
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.action.ActionFilterKey
import tcla.contexts.tcla.core.domain.action.ActionRepository
import tcla.contexts.tcla.core.domain.action.model.Action
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.QuestionToThinkAbout
import tcla.contexts.tcla.core.domain.questiontothinkabout.model.questionsToThinkAboutMap
import tcla.contexts.tcla.core.domain.tcldriver.TclDriverRepository
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessment
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessmentRepository
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator
import java.util.UUID

@Named
class SqlActionRepository(
    private val jpaActionRepository: JpaActionRepository,
    private val jpaAssessmentRepository: JpaAssessmentRepository,
    private val tclDriverRepository: TclDriverRepository,
) : ActionRepository {
    override fun exists(id: Action.ActionId): Either<NonEmptyList<Failure>, Boolean> =
        Either.catch { jpaActionRepository.existsById(id.value) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { exists -> exists.right() }

    override fun save(action: Action): Either<NonEmptyList<Failure>, Action> =
        Either.catch { jpaAssessmentRepository.findByIdOrNull(action.assessmentId.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaAssessment: JpaAssessment? ->
                when (jpaAssessment) {
                    null -> Failure.DataWasExpectedToExist.Assessment.nel().left()
                    else -> jpaAssessment.right()
                }
            }.flatMap { jpaAssessment: JpaAssessment -> action.toJpa(jpaAssessment).right() }
            .flatMap { jpaAction: JpaAction ->
                Either.catch { jpaActionRepository.save(jpaAction) }
                    .mapLeft { Failure.DatabaseException(it).nel() }
            }.flatMap { savedJpaAction: JpaAction -> savedJpaAction.toDomain() }

    private fun JpaAction.toDomain(): Either<NonEmptyList<Failure>, Action> =
        targetQuestionsToThinkAboutToDomain(targetQuestionsToThinkAbout)
            .flatMap { targetQuestionsToThinkAbout: Action.TargetQuestionsToThinkAbout ->
                targetTclDriversToDomain(targetTclDrivers)
                    .flatMap { targetTclDrivers: Action.TargetTclDrivers ->
                        toDomain(targetQuestionsToThinkAbout, targetTclDrivers).right()
                    }
            }

    override fun find(id: Action.ActionId): Either<NonEmptyList<Failure>, Action> =
        Either.catch { jpaActionRepository.findByIdOrNull(id.value) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaAction: JpaAction? ->
                when(jpaAction) {
                    null -> Failure.EntityNotFound.Action.nel().left()
                    else -> jpaAction.right()
                }
            }.flatMap { it.toDomain() }

    override fun search(filter: Filter<ActionFilterKey>?): Either<NonEmptyList<Failure>, List<Action>> =
        findJpaActions(filter)
            .flatMap { jpaActions: List<JpaAction> ->
                jpaActions
                    .map { jpaAction -> jpaAction.toDomain() }
                    .flattenOrAccumulate()
            }

    private fun findJpaActions(filter: Filter<ActionFilterKey>?): Either<NonEmptyList<Failure>, List<JpaAction>> =
        when (filter) {
            null -> Either.catch { jpaActionRepository.findAll() }
                .mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(exception = throwable)) }

            else -> when (filter) {
                is ManyValuesFilter<ActionFilterKey, *> -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Action).left()

                is OneValueFilter<ActionFilterKey, *> -> when (filter.key) {
                    ActionFilterKey.ASSESSMENT -> when (val value = filter.value) {
                        is AssessmentId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaActionRepository.findAllByAssessment_Id(value.uuid)
                            }.mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(throwable)) }

                            Operator.NaryOperator.In -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Action).left()
                        }

                        else -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Action).left()
                    }
                }
            }
        }

    private fun targetQuestionsToThinkAboutToDomain(targetQuestionsToThinkAbout: String): Either<NonEmptyList<Failure>, Action.TargetQuestionsToThinkAbout> =
        when(targetQuestionsToThinkAbout.isEmpty()) {
            true -> emptyList()
            false -> targetQuestionsToThinkAbout.split(',').mapOrAccumulate { uuid: String ->
                Either.catch { UUID.fromString(uuid) }
                    .mapLeft { Failure.UnableToTransformIntoDomainData.Action }
                    .bind()
            }
        }.flatMap { uuids: List<UUID> ->
            uuids.mapOrAccumulate { uuid: UUID ->
                questionsToThinkAboutMap.value.values.flatten().find { it.id.value == uuid }
                    .let { questionToThinkAbout: QuestionToThinkAbout? ->
                        when (questionToThinkAbout == null) {
                            true -> Failure.DataWasExpectedToExist.QuestionToThinkAbout.left()
                            false -> questionToThinkAbout.right()
                        }
                    }.bind()
            }.flatMap { questionsToThinkAbout: List<QuestionToThinkAbout> ->
                Action.TargetQuestionsToThinkAbout(
                    value = questionsToThinkAbout.toSet()
                ).right()
            }
        }

    private fun emptyList(): Either<NonEmptyList<Failure>, List<UUID>> = listOf<UUID>().right()

    private fun targetTclDriversToDomain(targetTclDrivers: String): Either<NonEmptyList<Failure>, Action.TargetTclDrivers> =
        when(targetTclDrivers.isEmpty()) {
            true -> emptyList()
            false -> targetTclDrivers.split(',').mapOrAccumulate { uuid: String ->
                Either.catch { UUID.fromString(uuid) }
                    .mapLeft { Failure.UnableToTransformIntoDomainData.Action }
                    .bind()
            }
        }.flatMap { uuids: List<UUID> ->
            tclDriverRepository.findAllById(uuids.map { TclDriverId(it) })
                .flatMap { tclDrivers: List<TclDriver> ->
                    Action.TargetTclDrivers(
                        value = tclDrivers.toSet()
                    ).right()
                }
        }
}


