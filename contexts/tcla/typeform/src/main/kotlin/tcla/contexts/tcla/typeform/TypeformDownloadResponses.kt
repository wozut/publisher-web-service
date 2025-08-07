package tcla.contexts.tcla.typeform

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import tcla.contexts.tcla.core.application.failures.DownloadResponsesFailure
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefilling.downloadquestionnairefillings.downloadresponses.DownloadResponses
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling

@Named
class TypeformDownloadResponses(
    @Value("\${typeform.base-url}") private val typeformBaseUrl: String,
    @Value("\${typeform.access-token}") private val typeformAccessToken: String,
    private val okHttpClient: OkHttpClient
) : DownloadResponses {

    override fun execute(externalQuestionnaireId: ExternalQuestionnaireId?, questions: Set<Question>): Either<DownloadResponsesFailure, Set<QuestionnaireFilling>> =
        builderWithUrl(externalQuestionnaireId)
            .flatMap { builder ->
                builder
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer $typeformAccessToken")
                    .build().right()
            }.flatMap { request: Request ->
                Either.catch { okHttpClient.newCall(request).execute() }
                    .mapLeft { throwable -> DownloadResponsesFailure.RequestNotExecuted(throwable) }
                    .flatMap { response ->
                        response.use { it: Response ->
                            when (it.code) {
                                200 -> Either.catch {
                                    responsesToQuestionnaireFillings(
                                        it.body!!.string(),
                                        questions
                                    )
                                }.mapLeft { DownloadResponsesFailure.UnexpectedResponse }

                                else -> DownloadResponsesFailure.ResponseError(it.code, it.body?.string()).left()
                            }
                        }
                    }
            }

    private fun builderWithUrl(externalQuestionnaireId: ExternalQuestionnaireId?): Either<DownloadResponsesFailure, Request.Builder> {
        val formId: ExternalQuestionnaireId =
            externalQuestionnaireId ?: return DownloadResponsesFailure.InvalidUrl.left()

        return Either.catch {
            Request.Builder().url("${typeformBaseUrl}/forms/${formId.value}/responses?page_size=1000&completed=true")
        }.mapLeft { DownloadResponsesFailure.InvalidUrl }
    }
}
