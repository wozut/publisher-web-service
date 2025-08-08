package tcla.contexts.tcla.database.springdatajpa.questionnairefillingforanalysis

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.mapOrAccumulate
import arrow.core.nel
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.QuestionnaireFillingForAnalysisFilterKey
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.QuestionnaireFillingForAnalysisRepository
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysis
import tcla.contexts.tcla.database.springdatajpa.questionnairefilling.JpaQuestionnaireFilling
import tcla.contexts.tcla.database.springdatajpa.questionnairefilling.JpaQuestionnaireFillingRepository
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator

@Named
class SqlQuestionnaireFillingForAnalysisRepository(
    private val jpaQuestionnaireFillingRepository: JpaQuestionnaireFillingRepository
) : QuestionnaireFillingForAnalysisRepository {
    override fun search(filter: Filter<QuestionnaireFillingForAnalysisFilterKey>?): Either<NonEmptyList<Failure>, List<QuestionnaireFillingForAnalysis>> =
        when (filter) {
            null -> findAllRecords()
            else -> findAllRecordsWithFilter(filter)
        }.flatMap { jpaQuestionnaireFillings: List<JpaQuestionnaireFilling> -> jpaQuestionnaireFillings.toDomain() }

    private fun List<JpaQuestionnaireFilling>.toDomain(): Either<NonEmptyList<Failure>,List<QuestionnaireFillingForAnalysis>> =
        mapOrAccumulate { jpaQuestionnaireFilling -> jpaQuestionnaireFilling.toDomainForAnalysis().bindNel() }

    private fun findAllRecords(): Either<NonEmptyList<Failure>, MutableList<JpaQuestionnaireFilling>> =
        Either.catch { jpaQuestionnaireFillingRepository.findAll() }
            .mapLeft { throwable -> Failure.DatabaseException(throwable).nel() }

    private fun findAllRecordsWithFilter(filter: Filter<QuestionnaireFillingForAnalysisFilterKey>): Either<NonEmptyList<Failure>, List<JpaQuestionnaireFilling>> =
        when (filter) {
            is ManyValuesFilter<QuestionnaireFillingForAnalysisFilterKey, *> -> Failure.UnsupportedDatabaseFilter.Response.nel().left()
            is OneValueFilter<QuestionnaireFillingForAnalysisFilterKey, *> -> {
                when (filter.key) {
                    QuestionnaireFillingForAnalysisFilterKey.QUESTIONNAIRE -> {
                        when (val value = filter.value) {
                            is QuestionnaireId -> {
                                when (filter.operator) {
                                    Operator.BinaryOperator.Equal -> {
                                        Either.catch {
                                            jpaQuestionnaireFillingRepository.findAllByQuestionnaire_Id(value.uuid)
                                        }.mapLeft { throwable ->
                                            Failure.DatabaseException(throwable).nel()
                                        }
                                    }

                                    Operator.NaryOperator.In -> Failure.UnsupportedDatabaseFilter.Response.nel().left()
                                }
                            }

                            else -> Failure.UnsupportedDatabaseFilter.Response.nel().left()
                        }
                    }
                }
            }
        }

}
