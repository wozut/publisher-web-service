package tcla.contexts.accounts.core.application.account.update

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.accounts.core.domain.AccountsRequestIsAuthenticatedRule
import tcla.contexts.accounts.core.domain.account.AccountRepository
import tcla.contexts.accounts.core.domain.account.model.Account
import tcla.contexts.accounts.core.domain.account.model.AccountId
import tcla.contexts.accounts.core.domain.account.model.Name
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class UpdateAccountCommandHandler(
    private val transactionExecutor: TransactionExecutor,
    private val accountRepository: AccountRepository,
    private val accountsRequestIsAuthenticatedRule: AccountsRequestIsAuthenticatedRule
) {
    fun execute(command: UpdateAccountCommand): Either<UpdateAccountFailure, UpdateAccountSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            ensureRequestIsAuthenticated()
                .flatMap { requesterId ->
                    when (command.id) {
                        "mine" -> AccountId(requesterId)
                            .let { accountRepository.find(it).mapLeft { TODO() } }
                            .flatMap { account -> account.updateNameIfNeeded(command.fields) }
                            .flatMap { account -> ensureNameIsDifferentFromEmail(account) }
                            .flatMap { account -> accountRepository.save(account).mapLeft { TODO() } }
                            .flatMap { UpdateAccountSuccess.right() }

                        else -> TODO()
                    }
                }
        }

    private fun ensureNameIsDifferentFromEmail(account: Account): Either<UpdateAccountFailure, Account> =
        when (account.name.string == account.email.string) {
            true -> UpdateAccountFailure.NameMustBeDifferentFromEmail.left()
            false -> account.right()
        }

    private fun ensureRequestIsAuthenticated(): Either<UpdateAccountFailure, String> =
        accountsRequestIsAuthenticatedRule.ensure()
            .mapLeft { UpdateAccountFailure.RequestNotAuthenticated }

    private fun Account.updateNameIfNeeded(
        fields: HashMap<String, String?>
    ): Either<UpdateAccountFailure, Account> {
        val key = "name"
        return when (fields.containsKey(key)) {
            true -> {
                val nameValue: String? = fields[key]
                when (nameValue.isNullOrBlank()) {
                    true -> UpdateAccountFailure.NameMustNotBeNullNeitherBlank.left()
                    false -> this.updateName(name = Name(nameValue)).right()
                }
            }

            false -> right()
        }
    }
}
