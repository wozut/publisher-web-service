package tcla.contexts.tcla.database.springdatajpa.respondent

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.flattenOrAccumulate
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import org.springframework.data.repository.findByIdOrNull
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.respondent.RespondentFilterKey
import tcla.contexts.tcla.core.domain.respondent.RespondentRepository
import tcla.contexts.tcla.core.domain.respondent.model.Respondent
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessmentRepository
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator

@Named
class SqlRespondentRepository(
    private val jpaRespondentRepository: JpaRespondentRepository,
    private val jpaAssessmentRepository: JpaAssessmentRepository
) : RespondentRepository {

    override fun exists(id: RespondentId): Either<NonEmptyList<Failure>, Boolean> =
        Either.catch { jpaRespondentRepository.existsById(id.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { exists -> exists.right() }

    override fun save(respondent: Respondent): Either<NonEmptyList<Failure>, Respondent> =
        Either.catch { jpaAssessmentRepository.findByIdOrNull(respondent.assessmentId.uuid) }
            .mapLeft { Failure.DatabaseException(it).nel() }
            .flatMap { jpaAssessment ->
                when (jpaAssessment) {
                    null -> Failure.DataWasExpectedToExist.Assessment.nel().left()
                    else -> jpaAssessment.right()
                }
            }.flatMap { jpaAssessment ->
                respondent.toJpa(jpaAssessment)
                    .let { jpaRespondent ->
                        Either.catch { jpaRespondentRepository.save(jpaRespondent) }
                            .mapLeft { Failure.DatabaseException(it).nel() }
                    }.flatMap { jpaRespondent -> jpaRespondent.toDomain() }
            }

    override fun search(filter: Filter<RespondentFilterKey>?): Either<NonEmptyList<Failure>, List<Respondent>> =
        findJpaRespondents(filter)
            .flatMap { jpaRespondents: List<JpaRespondent> ->
                jpaRespondents
                    .map { jpaRespondent -> jpaRespondent.toDomain() }
                    .flattenOrAccumulate()
            }

    private fun findJpaRespondents(filter: Filter<RespondentFilterKey>?): Either<NonEmptyList<Failure>, List<JpaRespondent>> =
        when (filter) {
            null -> Either.catch { jpaRespondentRepository.findAll() }
                .mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(throwable)) }

            else -> when (filter) {
                is ManyValuesFilter<RespondentFilterKey, *> -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Respondent).left()

                is OneValueFilter<RespondentFilterKey, *> -> when (filter.key) {
                    RespondentFilterKey.ASSESSMENT -> when (val value = filter.value) {
                        is AssessmentId -> when (filter.operator) {
                            Operator.BinaryOperator.Equal -> Either.catch {
                                jpaRespondentRepository.findAllByAssessment_Id(value.uuid)
                            }.mapLeft { throwable -> nonEmptyListOf(Failure.DatabaseException(throwable)) }

                            Operator.NaryOperator.In -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Respondent).left()
                        }

                        else -> nonEmptyListOf(Failure.UnsupportedDatabaseFilter.Respondent).left()
                    }
                }
            }
        }
}
