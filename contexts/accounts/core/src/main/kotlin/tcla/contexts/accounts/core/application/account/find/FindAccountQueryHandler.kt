package tcla.contexts.accounts.core.application.account.find

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.accounts.core.application.failures.FindAccountFailure
import tcla.contexts.accounts.core.domain.AccountsRequestIsAuthenticatedRule
import tcla.contexts.accounts.core.domain.account.AccountRepository
import tcla.contexts.accounts.core.domain.account.model.Account
import tcla.contexts.accounts.core.domain.account.model.AccountId
import tcla.libraries.logging.logError
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class FindAccountQueryHandler(
    private val transactionExecutor: TransactionExecutor,
    private val accountRepository: AccountRepository,
    private val accountsRequestIsAuthenticatedRule: AccountsRequestIsAuthenticatedRule
) {

    fun execute(query: FindAccountQuery): Either<FindAccountFailure, FindAccountSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            ensureRequestIsAuthenticated()
                .flatMap { requesterId ->
                    when (query.id) {
                        "mine" -> AccountId(requesterId).right()
                        else -> ensureRequesterOwnsAccountToBeFound(query.id, requesterId)
                    }
                }.flatMap { accountId -> find(accountId) }
                .flatMap { account -> FindAccountSuccess(account).right() }
        }

    /**
     * This function is reserved for requests coming from the application itself.
     *
     * Important: not to call directly this function when the requester is a user outside the application
     */
    fun find(accountId: AccountId): Either<FindAccountFailure, Account> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            accountRepository.find(accountId)
                .onLeft { failure ->
                    when (failure) {
                        FindAccountFailure.NotFound -> this.logError("${failure}. Account id: ${accountId.string}")
                        FindAccountFailure.RequestNotAuthenticated -> this.logError("$failure. Account id: ${accountId.string}")
                        is FindAccountFailure.TechnicalException -> this.logError("$failure. Throwable: ${failure.exception}")
                        FindAccountFailure.Unauthorized -> this.logError("$failure. Account id: ${accountId.string}")
                    }
                }
        }

    private fun ensureRequesterOwnsAccountToBeFound(
        accountToBeFound: String,
        requesterId: String
    ): Either<FindAccountFailure.Unauthorized, AccountId> =
        when (accountToBeFound) {
            requesterId -> AccountId(requesterId).right()
            else -> FindAccountFailure.Unauthorized.left()
        }

    private fun ensureRequestIsAuthenticated(): Either<FindAccountFailure, String> =
        accountsRequestIsAuthenticatedRule.ensure()
            .mapLeft { FindAccountFailure.RequestNotAuthenticated }
}
