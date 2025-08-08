package tcla.contexts.tcla.database.springdatajpa.tcldriverscore

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.ResultsShareableToken
import tcla.contexts.tcla.core.domain.tcldriverscore.SaveAllTclDriverScoresSuccess
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreFilterKey
import tcla.contexts.tcla.core.domain.tcldriverscore.TclDriverScoreRepository
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreId
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessment
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessmentRepository
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator

@Named
class SqlTclDriverScoreRepository(
    private val jpaTclDriverScoreRepository: JpaTclDriverScoreRepository,
    private val jpaAssessmentRepository: JpaAssessmentRepository
) : TclDriverScoreRepository {
    override fun noneExists(tclDriverScoreIds: List<TclDriverScoreId>): Either<Failure, Boolean> =
        tclDriverScoreIds.map { tclDriverScoreId -> tclDriverScoreId.value }
            .let { ids ->
                Either.catch { jpaTclDriverScoreRepository.findAllById(ids) }
                    .mapLeft(Failure::DatabaseException)
                    .flatMap { jpaTclDriverScores -> jpaTclDriverScores.isEmpty().right() }
            }

    override fun saveAll(tclDriverScores: List<TclDriverScore>): Either<NonEmptyList<Failure>, SaveAllTclDriverScoresSuccess> =
        tclDriverScores.toJpa()
            .flatMap { jpaTclDriverScores ->
                Either.catch { jpaTclDriverScoreRepository.saveAll(jpaTclDriverScores) }
                    .mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(throwable)) }
                    .flatMap { SaveAllTclDriverScoresSuccess.right() }
            }

    private fun List<TclDriverScore>.toJpa(): Either<NonEmptyList<Failure>, List<JpaTclDriverScore>> =
        mapOrAccumulate { tclDriverScore ->
            Either.catch { jpaAssessmentRepository.findByIdOrNull(tclDriverScore.assessmentId.uuid) }
                .mapLeft { throwable -> Failure.DatabaseException(throwable) }
                .flatMap { jpaAssessment: JpaAssessment? ->
                    when (jpaAssessment) {
                        null -> Failure.EntityNotFound.Assessment.left()
                        else -> tclDriverScore.toJpa(jpaAssessment).right()
                    }
                }.bind()
        }

    override fun search(filter: Filter<TclDriverScoreFilterKey>?): Either<NonEmptyList<Failure>, List<TclDriverScore>> =
        findJpaTclDriverScores(filter)
            .flatMap { jpaTclDriverScores: List<JpaTclDriverScore> ->
                jpaTclDriverScores.map { jpaTclDriverScore -> jpaTclDriverScore.toDomain() }.right()
            }

    override fun deleteByAssessmentId(id: AssessmentId): Either<NonEmptyList<Failure>, Unit> =
        Either.catch { jpaTclDriverScoreRepository.deleteAllByAssessment_Id(id.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }

    private fun findJpaTclDriverScores(filter: Filter<TclDriverScoreFilterKey>?): Either<NonEmptyList<Failure>, List<JpaTclDriverScore>> =
        when (filter) {
            null -> Either.catch { jpaTclDriverScoreRepository.findAll() }
                .mapLeft { throwable -> Failure.DatabaseException(exception = throwable).nel() }

            else -> when (filter) {
                is ManyValuesFilter<TclDriverScoreFilterKey, *> -> Failure.UnsupportedDatabaseFilter.TclDriverScore.nel()
                    .left()

                is OneValueFilter<TclDriverScoreFilterKey, *> -> when (filter.key) {
                    TclDriverScoreFilterKey.ASSESSMENT -> when (val value = filter.value) {
                        is AssessmentId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaTclDriverScoreRepository.findAllByAssessment_Id(value.uuid)
                            }.mapLeft { throwable -> Failure.DatabaseException(throwable).nel() }

                            Operator.NaryOperator.In -> Failure.UnsupportedDatabaseFilter.TclDriverScore.nel().left()
                        }

                        else -> Failure.UnsupportedDatabaseFilter.TclDriverScore.nel().left()
                    }

                    TclDriverScoreFilterKey.RESULTS_SHAREABLE_TOKEN -> when (val value = filter.value) {
                        is ResultsShareableToken -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaTclDriverScoreRepository.findAllByAssessment_ResultsShareableToken(value.token)
                            }.mapLeft { throwable -> Failure.DatabaseException(throwable).nel() }

                            Operator.NaryOperator.In -> Failure.UnsupportedDatabaseFilter.TclDriverScore.nel().left()
                        }

                        else -> Failure.UnsupportedDatabaseFilter.TclDriverScore.nel().left()
                    }
                }
            }
        }
}

private fun TclDriverScore.toJpa(jpaAssessment: JpaAssessment): JpaTclDriverScore =
    JpaTclDriverScore(
        id = this.id.value,
        value = this.value.value,
        tclDriverId = this.tclDriverId.value,
        assessment = jpaAssessment
    )
