package tcla.contexts.tcla.database.springdatajpa.questionnairefilling

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.response.search.SearchResponsesFailure
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefilling.QuestionnaireFillingRepository
import tcla.contexts.tcla.core.domain.questionnairefilling.ResponseFilterKey
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling
import tcla.libraries.search.Filter
import tcla.libraries.search.ManyValuesFilter
import tcla.libraries.search.OneValueFilter
import tcla.libraries.search.Operator

@Named
class SqlQuestionnaireFillingRepository(
    private val jpaQuestionnaireFillingRepository: JpaQuestionnaireFillingRepository
) : QuestionnaireFillingRepository {
    override fun search(filter: Filter<ResponseFilterKey>): Either<SearchResponsesFailure, List<QuestionnaireFilling>> =
        when (filter) {
            is ManyValuesFilter<ResponseFilterKey, *> -> SearchResponsesFailure.UnsupportedDatabaseFilter.left()

            is OneValueFilter<ResponseFilterKey, *> -> when (filter.key) {
                ResponseFilterKey.QUESTIONNAIRE -> when (val value = filter.value) {
                    is QuestionnaireId -> when (filter.operator) {
                        Operator.BinaryOperator.Equal -> Either.catch {
                            jpaQuestionnaireFillingRepository.findAllByQuestionnaire_Id(value.uuid)
                        }.mapLeft { throwable -> SearchResponsesFailure.DatabaseException(throwable) }
                            .flatMap { responses: List<JpaQuestionnaireFilling> ->
                                responses.map { jpaQuestionnaireFilling -> jpaQuestionnaireFilling.toDomain() }.right()
                            }

                        Operator.NaryOperator.In -> SearchResponsesFailure.UnsupportedDatabaseFilter.left()
                    }

                    else -> SearchResponsesFailure.UnsupportedDatabaseFilter.left()
                }
            }
        }


}
