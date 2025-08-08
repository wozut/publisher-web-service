package tcla.contexts.tcla.core.domain.runtclmodel

sealed class RunTclModelFailure {
    data object InvalidURL : RunTclModelFailure()
    data object InsufficientCells : RunTclModelFailure()
    data object InsufficientRows : RunTclModelFailure()
    data object UnableToCreateWorkbook : RunTclModelFailure()
    data object UnableToGetSheet : RunTclModelFailure()
    data object UnsupportedData : RunTclModelFailure()
    data class TclModelRun(val statusCode: Int, val body: String) : RunTclModelFailure()
    data class TclModelCallException(val throwable: Throwable) : RunTclModelFailure()
}
