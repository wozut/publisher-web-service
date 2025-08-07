package tcla.contexts.tcla.core.domain.tcldriver

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId


interface TclDriverRepository {
    fun search(): Either<NonEmptyList<Failure>, List<TclDriver>>

    fun findAllById(ids: List<TclDriverId>): Either<NonEmptyList<Failure>, List<TclDriver>>
}
