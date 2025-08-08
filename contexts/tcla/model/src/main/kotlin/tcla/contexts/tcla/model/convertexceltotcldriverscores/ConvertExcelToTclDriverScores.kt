package tcla.contexts.tcla.model.convertexceltotcldriverscores

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreId
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreValue
import java.io.ByteArrayInputStream
import java.util.*

@Named
class ConvertExcelToTclDriverScores {

    companion object {
        val rowPositionToTclDriverIdAssociation = mapOf(1 to TclDriverId(UUID.fromString("0fc00659-527b-4ad3-b449-007c9bb82a67")), 2 to TclDriverId(UUID.fromString("9607872f-059d-41ec-9662-f17778e5bc3a")), 3 to TclDriverId(UUID.fromString("3c2f2271-0551-454c-ac7b-74e2909b7159")), 4 to TclDriverId(UUID.fromString("583d82c7-96a3-4b6f-a71e-e45f46a949f7")), 5 to TclDriverId(UUID.fromString("66f9073f-43e7-4362-848e-010f4f53722c")), 6 to TclDriverId(UUID.fromString("4776965b-ae88-461f-bfe9-f2396d070fb0")), 7 to TclDriverId(UUID.fromString("adcbdd72-7727-4b3f-b630-3ef321fda248")), 8 to TclDriverId(UUID.fromString("b0821f0c-5708-421c-a3b8-6204ae11af56")), 9 to TclDriverId(UUID.fromString("fffb132c-154b-4f70-9290-0a8302e25e63")), 10 to TclDriverId(UUID.fromString("9936e560-1b43-4df1-8f33-ccee53609e08")), 11 to TclDriverId(UUID.fromString("80730316-1903-46e4-b824-f808012634dd")), 12 to TclDriverId(UUID.fromString("d9922cef-111c-4247-8cfc-62026625c783")), 13 to TclDriverId(UUID.fromString("e451865d-0e18-4209-b1fa-5efeffe5d2b6")), 14 to TclDriverId(UUID.fromString("7e1df8e7-7954-478f-b6d8-17a1cc94c3fc")), 15 to TclDriverId(UUID.fromString("56e29585-f823-47d1-990c-b19bbb8e1c39")), 16 to TclDriverId(UUID.fromString("d2f31415-9aa0-46e3-bde2-8ac550ef9ec3")), 17 to TclDriverId(UUID.fromString("373a00c8-07f0-4608-b3d6-fed22012ccab")), 18 to TclDriverId(UUID.fromString("d74d9b22-1e11-43df-bd1d-5c278e738ce4")), 19 to TclDriverId(UUID.fromString("9329f3ef-5c04-4956-ae32-14b517239d68")), 20 to TclDriverId(UUID.fromString("15e40f37-d863-467e-9f86-3818e165ad22")), 21 to TclDriverId(UUID.fromString("6635f911-daf8-4d1e-8a4b-6da9f9107201")), 22 to TclDriverId(UUID.fromString("eae15c6c-235b-4299-bc2f-5d5719a988fb")), 23 to TclDriverId(UUID.fromString("33a76fe3-3d60-484d-a44f-b9c038c35c54")), 24 to TclDriverId(UUID.fromString("4156000d-837e-4820-bc72-56cf73be8e51")), 25 to TclDriverId(UUID.fromString("cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c")), 26 to TclDriverId(UUID.fromString("b1e1e80d-a4eb-497a-af14-5591c06abb1c")), 27 to TclDriverId(UUID.fromString("101bbde8-228c-4056-883b-add27306bb9c")), 28 to TclDriverId(UUID.fromString("efbd44d5-b79a-4e57-bfdf-d761aff3250b")), 29 to TclDriverId(UUID.fromString("8ddef539-54f8-413c-87e5-a99eacd1db68")), 30 to TclDriverId(UUID.fromString("00cf4195-6bf4-4d5f-94dd-b045b4e735db")), 31 to TclDriverId(UUID.fromString("b4ab706b-6f5f-4ac4-bbea-23f822a0b711")), 32 to TclDriverId(UUID.fromString("8e528d6a-5d2f-4287-9f9d-bcff50c76cdf")), 33 to TclDriverId(UUID.fromString("0c5c9ab1-6c3d-40f1-a700-cc677c26681f")), 34 to TclDriverId(UUID.fromString("8c95ba02-2fc1-46a4-b2c6-a4d7a3eb7524")), 35 to TclDriverId(UUID.fromString("e01fb140-36f5-4abc-b45b-ace3d4b8e43f")), 36 to TclDriverId(UUID.fromString("9416fd0a-636e-4cda-a07c-edb93c888ee2")), 37 to TclDriverId(UUID.fromString("5a9f12ed-3e2d-445c-b699-8b426b1d7f9f")), 38 to TclDriverId(UUID.fromString("d5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab")), 39 to TclDriverId(UUID.fromString("894b7fab-c3ae-43e5-96a8-b36952b698ec")), 40 to TclDriverId(UUID.fromString("54986fdc-e3fd-4ff4-a776-cf8c5a206021")), 41 to TclDriverId(UUID.fromString("43792379-6c2e-4940-ba10-423a551f4741")))

        private const val NEEDED_NUMBER_OF_ROWS: Int = 42
        private val rowNumbers = 1 until NEEDED_NUMBER_OF_ROWS
    }

    fun execute(
        excelByteArray: ByteArray,
        assessmentId: AssessmentId
    ): Either<ConvertExcelToTclDriverScoresFailure, ConvertExcelToTclDriverScoresSuccess> =
        ByteArrayInputStream(excelByteArray).right()
            .flatMap { byteArrayInputStream -> loadWorkbook(byteArrayInputStream) }
            .flatMap { workbook -> getSheet(workbook) }
            .flatMap { sheet: Sheet -> ensureThereAreCellsEnough(sheet) }
            .flatMap { sheet: Sheet -> rowNumbers.map { rowNumber -> sheet.getRow(rowNumber) }.right() }
            .flatMap { rows: List<Row> -> rows.toTclDriverScores(assessmentId) }
            .flatMap { tclDriverScores -> ConvertExcelToTclDriverScoresSuccess(tclDriverScores).right() }

    private fun List<Row>.toTclDriverScores(
        assessmentId: AssessmentId
    ): Either<ConvertExcelToTclDriverScoresFailure, List<TclDriverScore>> =
        Either.catch { map { row: Row -> row.toTclDriverScore(assessmentId) } }
            .mapLeft { ConvertExcelToTclDriverScoresFailure.UnsupportedData }

    private fun ensureThereAreCellsEnough(sheet: Sheet): Either<ConvertExcelToTclDriverScoresFailure, Sheet> =
        when (sheet.map { it }.size) {
            in NEEDED_NUMBER_OF_ROWS..Int.MAX_VALUE -> sheet.right()
            else -> ConvertExcelToTclDriverScoresFailure.InsufficientRows.left()
        }.flatMap {
            val allNeededCellsExist = sheet.all { row -> row.getCell(1) != null }
            when (allNeededCellsExist) {
                true -> sheet.right()
                false -> ConvertExcelToTclDriverScoresFailure.InsufficientCells.left()
            }
        }

    private fun loadWorkbook(byteArrayInputStream: ByteArrayInputStream): Either<ConvertExcelToTclDriverScoresFailure, Workbook> =
        Either.catch { WorkbookFactory.create(byteArrayInputStream) }
            .mapLeft { throwable -> ConvertExcelToTclDriverScoresFailure.UnableToCreateWorkbook(throwable) }

    private fun getSheet(workbook: Workbook): Either<ConvertExcelToTclDriverScoresFailure.UnableToGetSheet, Sheet> =
        when (val sheet = workbook.getSheet("Average of construct, cluster")) {
            null -> ConvertExcelToTclDriverScoresFailure.UnableToGetSheet.left()
            else -> sheet.right()
        }

    private fun Row.toTclDriverScore(assessmentId: AssessmentId): TclDriverScore =
        getCell(1)!!
            .let { tclDriverScoreCell ->
                when (tclDriverScoreCell.cellType) {
                    CellType._NONE -> throw RuntimeException("Unsupported data")
                    CellType.NUMERIC -> tclDriverScoreCell.numericCellValue
                    CellType.STRING -> tclDriverScoreCell.stringCellValue.toDouble()
                    CellType.FORMULA -> throw RuntimeException("Unsupported data")
                    CellType.BLANK -> throw RuntimeException("Unsupported data")
                    CellType.BOOLEAN -> throw RuntimeException("Unsupported data")
                    CellType.ERROR -> throw RuntimeException("Unsupported data")
                    null -> throw RuntimeException("Unsupported data")
                }
            }.let { tclDriverScoreValue ->
                TclDriverScore(
                    id = TclDriverScoreId(UUID.randomUUID()),
                    assessmentId = assessmentId,
                    tclDriverId = rowPositionToTclDriverIdAssociation[rowNum]!!,
                    value = TclDriverScoreValue(tclDriverScoreValue)
                )
            }
}
