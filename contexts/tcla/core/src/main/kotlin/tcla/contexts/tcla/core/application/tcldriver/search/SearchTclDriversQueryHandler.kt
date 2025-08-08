package tcla.contexts.tcla.core.application.tcldriver.search

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.tcldriver.TclDriverRepository
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@Named
class SearchTclDriversQueryHandler(
    private val transactionExecutor: TransactionExecutor,
    private val tclDriverRepository: TclDriverRepository
) {
    fun execute(query: SearchTclDriversQuery): Either<NonEmptyList<Failure>, SearchTclDriversSuccess> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            tclDriverRepository.search().flatMap { tclDrivers -> SearchTclDriversSuccess(tclDrivers).right() }
        }
}
