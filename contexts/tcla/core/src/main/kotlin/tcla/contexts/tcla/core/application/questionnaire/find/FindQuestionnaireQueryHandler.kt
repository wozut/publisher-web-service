package tcla.contexts.tcla.core.application.questionnaire.find

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questionnaire.QuestionnaireRepository
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import tcla.libraries.uuidvalidation.toUuid
import java.util.UUID

@Named
class FindQuestionnaireQueryHandler(
    private val questionnaireRepository: QuestionnaireRepository,
    private val transactionExecutor: TransactionExecutor
) {
    fun execute(query: FindQuestionnaireQuery): Either<NonEmptyList<Failure>, FindQuestionnaireSuccess> =
        transactionExecutor.transactional(IsolationLevel.READ_COMMITTED) {
            query.id.toUuid()
                .mapLeft { Failure.StringIsNotUuid.SurveyId.nel() }
                .flatMap { uuid: UUID -> questionnaireRepository.find(QuestionnaireId(uuid)) }
                .flatMap { FindQuestionnaireSuccess(it).right() }
        }
}
