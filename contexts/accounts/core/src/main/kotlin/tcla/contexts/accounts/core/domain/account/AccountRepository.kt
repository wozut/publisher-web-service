package tcla.contexts.accounts.core.domain.account

import arrow.core.Either
import tcla.contexts.accounts.core.application.failures.FindAccountFailure
import tcla.contexts.accounts.core.domain.account.model.Account
import tcla.contexts.accounts.core.domain.account.model.AccountId

interface AccountRepository {
    fun find(id: AccountId): Either<FindAccountFailure, Account>

    fun save(account: Account): Either<SaveAccountFailure, AccountId>
}
