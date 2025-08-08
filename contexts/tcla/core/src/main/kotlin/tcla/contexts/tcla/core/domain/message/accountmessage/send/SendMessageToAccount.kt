package tcla.contexts.tcla.core.domain.message.accountmessage.send

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.communications.core.application.sendpredefinedemail.Recipient
import tcla.contexts.communications.core.application.sendpredefinedemail.SendPredefinedEmailCommand
import tcla.contexts.communications.core.application.sendpredefinedemail.SendPredefinedEmailCommandHandler
import tcla.contexts.communications.core.application.sendpredefinedemail.SendPredefinedEmailFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.SendMessageToAccountFailure
import tcla.contexts.tcla.core.domain.PRODUCT_EMAIL
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.accountmessage.AccountMessageRepository
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage
import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessageId
import tcla.contexts.tcla.core.domain.teamowner.find.FindTeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwner
import tcla.contexts.tcla.core.domain.teamowner.model.TeamOwnerId
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.time.Instant

@Named
class SendMessageToAccount(
    private val transactionExecutor: TransactionExecutor,
    private val accountMessageRepository: AccountMessageRepository,
    private val findTeamOwner: FindTeamOwner,
    private val sendPredefinedEmailCommandHandler: SendPredefinedEmailCommandHandler
) {
    fun execute(accountMessageId: AccountMessageId): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            accountMessageRepository.find(accountMessageId)
                .flatMap { accountMessage -> ensureNotSentYet(accountMessage) }
                .flatMap { accountMessage -> ensureScheduledTimeIsBeforeNow(accountMessage) }
                .flatMap { accountMessage -> accountMessage.markAsSentAt(Instant.now()).right() }
                .flatMap { updatedAccountMessage -> accountMessageRepository.save(updatedAccountMessage) }
                .flatMap { accountMessage -> sendMessage(accountMessage) }
        }

    private fun sendMessage(accountMessage: AccountMessage): Either<NonEmptyList<Failure>, Unit> =
        when (accountMessage.channel) {
            Channel.EMAIL -> sendEmail(accountMessage)
        }

    private fun ensureScheduledTimeIsBeforeNow(accountMessage: AccountMessage): Either<NonEmptyList<Failure>, AccountMessage> =
        when (accountMessage.scheduledToBeSentAt.isBefore(Instant.now())) {
            true -> accountMessage.right()
            false -> SendMessageToAccountFailure.SendingTooEarly.nel().left()
        }

    private fun ensureNotSentYet(accountMessage: AccountMessage): Either<NonEmptyList<Failure>, AccountMessage> =
        when (accountMessage.actuallySentAt) {
            null -> accountMessage.right()
            else -> SendMessageToAccountFailure.AlreadySent.nel().left()
        }

    private fun sendEmail(accountMessage: AccountMessage): Either<NonEmptyList<Failure>, Unit> =
        findTeamOwner(accountMessage.accountId)
            .flatMap { teamOwner ->
                sendPredefinedEmailCommandHandler.execute(
                    SendPredefinedEmailCommand(
                        fromEmail = PRODUCT_EMAIL,
                        fromName = PRODUCT_NAME,
                        recipients = listOf(Recipient(name = teamOwner.name.string, email = teamOwner.email.string)),
                        templateId = accountMessage.type.toEmailTemplateId(),
                        variables = accountMessage.extraData
                    )
                ).mapLeft { _: SendPredefinedEmailFailure -> SendMessageToAccountFailure.SendPredefinedEmail.nel() }
            }.flatMap { Unit.right() }

    private fun findTeamOwner(teamOwnerId: String): Either<NonEmptyList<Failure>, TeamOwner> =
        findTeamOwner.execute(TeamOwnerId(teamOwnerId))
            .mapLeft { failure -> failure.nel() }
}
