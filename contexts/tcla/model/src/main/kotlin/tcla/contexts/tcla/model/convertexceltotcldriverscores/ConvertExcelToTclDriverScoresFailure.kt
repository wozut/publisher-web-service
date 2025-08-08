package tcla.contexts.tcla.model.convertexceltotcldriverscores

sealed class ConvertExcelToTclDriverScoresFailure {
    data object UnableToGetSheet : ConvertExcelToTclDriverScoresFailure()
    data object InsufficientRows : ConvertExcelToTclDriverScoresFailure()
    data object InsufficientCells : ConvertExcelToTclDriverScoresFailure()
    data object UnsupportedData : ConvertExcelToTclDriverScoresFailure()
    data class UnableToCreateWorkbook(val exception: Throwable) : ConvertExcelToTclDriverScoresFailure()
}
