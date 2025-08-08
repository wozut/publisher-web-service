package tcla.contexts.tcla.core.domain.message.teammembermessage.send

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
import tcla.contexts.tcla.core.application.failures.SendMessageToTeamMemberFailure
import tcla.contexts.tcla.core.domain.PRODUCT_EMAIL
import tcla.contexts.tcla.core.domain.PRODUCT_NAME
import tcla.contexts.tcla.core.domain.message.Channel
import tcla.contexts.tcla.core.domain.message.teammembermessage.TeamMemberMessageRepository
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage
import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessageId
import tcla.contexts.tcla.core.domain.teammember.TeamMemberRepository
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.time.Instant

@Named
class SendMessageToTeamMember(
    private val transactionExecutor: TransactionExecutor,
    private val teamMemberMessageRepository: TeamMemberMessageRepository,
    private val sendPredefinedEmailCommandHandler: SendPredefinedEmailCommandHandler,
    private val teamMemberRepository: TeamMemberRepository
) {
    fun execute(teamMemberMessageId: TeamMemberMessageId): Either<NonEmptyList<Failure>, Unit> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            find(teamMemberMessageId)
                .flatMap { teamMemberMessage -> ensureNotSentYet(teamMemberMessage) }
                .flatMap { teamMemberMessage -> ensureScheduledTimeIsBeforeNow(teamMemberMessage) }
                .flatMap { teamMemberMessage -> teamMemberMessage.markAsSentAt(Instant.now()).right() }
                .flatMap { updatedTeamMemberMessage -> save(updatedTeamMemberMessage) }
                .flatMap { teamMemberMessage -> sendMessage(teamMemberMessage) }
        }

    private fun find(teamMemberMessageId: TeamMemberMessageId): Either<NonEmptyList<Failure>, TeamMemberMessage> =
        teamMemberMessageRepository.find(teamMemberMessageId)

    private fun save(updatedTeamMemberMessage: TeamMemberMessage): Either<NonEmptyList<Failure>, TeamMemberMessage> =
        teamMemberMessageRepository.save(updatedTeamMemberMessage)

    private fun sendMessage(teamMemberMessage: TeamMemberMessage): Either<NonEmptyList<Failure>, Unit> =
        when (teamMemberMessage.channel) {
            Channel.EMAIL -> sendEmail(teamMemberMessage)
        }

    private fun ensureScheduledTimeIsBeforeNow(teamMemberMessage: TeamMemberMessage): Either<NonEmptyList<Failure>, TeamMemberMessage> =
        when (teamMemberMessage.scheduledToBeSentAt.isBefore(Instant.now())) {
            true -> teamMemberMessage.right()
            false -> SendMessageToTeamMemberFailure.SendingTooEarly.nel().left()
        }

    private fun ensureNotSentYet(teamMemberMessage: TeamMemberMessage): Either<NonEmptyList<Failure>, TeamMemberMessage> =
        when (teamMemberMessage.actuallySentAt) {
            null -> teamMemberMessage.right()
            else -> SendMessageToTeamMemberFailure.AlreadySent.nel().left()
        }

    private fun sendEmail(teamMemberMessage: TeamMemberMessage): Either<NonEmptyList<Failure>, Unit> =
        teamMemberRepository.find(teamMemberMessage.teamMemberId)
            .flatMap { teamMember ->
                sendPredefinedEmailCommandHandler.execute(
                    SendPredefinedEmailCommand(
                        fromEmail = PRODUCT_EMAIL,
                        fromName = PRODUCT_NAME,
                        recipients = listOf(Recipient(name = teamMember.name.string, email = teamMember.email.string)),
                        templateId = teamMemberMessage.type.toEmailTemplateId(),
                        variables = teamMemberMessage.extraData
                    )
                ).mapLeft { _: SendPredefinedEmailFailure -> SendMessageToTeamMemberFailure.SendPredefinedEmail.nel() }
            }.flatMap { Unit.right() }
}
