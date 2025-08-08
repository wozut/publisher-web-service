package tcla.contexts.tcla.model.runtclmodel

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysis
import tcla.contexts.tcla.core.domain.runtclmodel.RunTclModel
import tcla.contexts.tcla.core.domain.runtclmodel.RunTclModelFailure
import tcla.contexts.tcla.core.domain.runtclmodel.RunTclModelSuccess
import tcla.contexts.tcla.model.convertexceltotcldriverscores.ConvertExcelToTclDriverScores
import tcla.contexts.tcla.model.convertexceltotcldriverscores.ConvertExcelToTclDriverScoresFailure
import tcla.contexts.tcla.model.convertexceltotcldriverscores.ConvertExcelToTclDriverScoresSuccess
import tcla.libraries.logging.logError

@Named
class SpreadsheetBasedRunTclModel(
    @Value("\${tcla-model.base-url}") private val tclaModelBaseUrl: String,
    private val convertExcelToTclDriverScores: ConvertExcelToTclDriverScores,
    private val okHttpClient: OkHttpClient
) : RunTclModel {

    override fun execute(questionnaireFillingForAnalysisList: List<QuestionnaireFillingForAnalysis>, assessmentId: AssessmentId): Either<RunTclModelFailure, RunTclModelSuccess> =
        buildCsvFileContent(questionnaireFillingForAnalysisList).right()
            .flatMap { csvFileContent -> buildRequest(csvFileContent) }
            .flatMap { request: Request -> request.callTclModel() }
            .flatMap { response: Response -> response.extractBodyAsByteArray() }
            .flatMap { responseBodyAsByteArray: ByteArray -> responseBodyAsByteArray.toTclDriverScores(assessmentId) }
            .flatMap { success: ConvertExcelToTclDriverScoresSuccess -> success.tclDriverScores.right() }
            .flatMap { tclDriverScores -> RunTclModelSuccess(tclDriverScores).right() }

    private fun ByteArray.toTclDriverScores(
        assessmentId: AssessmentId
    ): Either<RunTclModelFailure, ConvertExcelToTclDriverScoresSuccess> =
        convertExcelToTclDriverScores.execute(this, assessmentId)
            .mapLeft { failure: ConvertExcelToTclDriverScoresFailure ->
                when (failure) {
                    ConvertExcelToTclDriverScoresFailure.InsufficientCells -> RunTclModelFailure.InsufficientCells
                    ConvertExcelToTclDriverScoresFailure.InsufficientRows -> RunTclModelFailure.InsufficientRows
                    is ConvertExcelToTclDriverScoresFailure.UnableToCreateWorkbook -> RunTclModelFailure.UnableToCreateWorkbook
                    ConvertExcelToTclDriverScoresFailure.UnableToGetSheet -> RunTclModelFailure.UnableToGetSheet
                    ConvertExcelToTclDriverScoresFailure.UnsupportedData -> RunTclModelFailure.UnsupportedData
                }
            }

    private fun Request.callTclModel(): Either<RunTclModelFailure.TclModelCallException, Response> =
        Either.catch { okHttpClient.newCall(this).execute() }
            .mapLeft { throwable -> RunTclModelFailure.TclModelCallException(throwable) }

    private fun Response.extractBodyAsByteArray(): Either<RunTclModelFailure.TclModelRun, ByteArray> =
        use { it: Response ->
            when (it.code) {
                200 -> it.body!!.bytes().right()
                else -> {
                    this.logError("Status code: ${it.code}. body: ${it.body!!.string()}")
                    RunTclModelFailure.TclModelRun(it.code, it.body!!.string()).left()
                }
            }
        }

    private fun buildRequest(csvFileContent: String): Either<RunTclModelFailure, Request> =
        MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "tcl_data_csv",
                filename = "tcl_data.csv",
                body = csvFileContent.toByteArray(Charsets.UTF_8)
                    .toRequestBody(contentType = "text/csv; charset=utf-8".toMediaType())
            ).build()
            .let { body ->
                Either.catch { Request.Builder().url("${tclaModelBaseUrl}/tcl-analysis") }
                    .mapLeft { RunTclModelFailure.InvalidURL }
                    .flatMap { it.post(body).build().right() }
            }
}
