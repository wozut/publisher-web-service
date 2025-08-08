package tcla.contexts.tcla.core.domain.respondent

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.Email
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.respondent.model.Respondent
import tcla.contexts.tcla.core.domain.respondent.model.RespondentId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.util.UUID

@Named
class CreateRespondent(
    private val transactionExecutor: TransactionExecutor,
    private val respondentRepository: RespondentRepository
) {

    fun execute(name: String, email: Email, assessmentId: AssessmentId): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            RespondentId(UUID.randomUUID())
                .let { respondentId ->
                    ensureRespondentWithGivenIdDoesNotExist(respondentId)
                        .flatMap {
                            Respondent(
                                id = respondentId,
                                name = name,
                                email = email,
                                assessmentId = assessmentId
                            ).right()
                        }.flatMap { respondent -> respondentRepository.save(respondent) }
                        .flatMap { Unit.right() }
                }
        }

    private fun ensureRespondentWithGivenIdDoesNotExist(respondentId: RespondentId): Either<NonEmptyList<Failure>, Unit> =
        respondentRepository.exists(respondentId)
            .flatMap { exists ->
                when (exists) {
                    true -> nonEmptyListOf(Failure.EntityAlreadyExists.Respondent).left()
                    false -> Unit.right()
                }
            }
}
