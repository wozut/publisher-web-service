package tcla.contexts.tcla.typeform

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import jakarta.inject.Named
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.publishexternalquestionnaire.PublishExternalQuestionnaire
import tcla.contexts.tcla.core.domain.questionnaire.publishexternalquestionnaire.PublishExternalQuestionnaireFailure

@Named
class TypeformPublishExternalQuestionnaire(
    @Value("\${typeform.base-url}") private val typeformBaseUrl: String,
    @Value("\${typeform.access-token}") private val typeformAccessToken: String,
    private val okHttpClient: OkHttpClient
) : PublishExternalQuestionnaire {
    override fun execute(externalQuestionnaireId: ExternalQuestionnaireId): Either<PublishExternalQuestionnaireFailure, Unit> {
        val body = requestBody()
        return builderWithUrl(externalQuestionnaireId.value)
            .flatMap { builder ->
                builder
                    .patch(body.toRequestBody("application/json".toMediaType()))
                    .addHeader("Authorization", "Bearer $typeformAccessToken")
                    .build().right()
            }.flatMap { request: Request ->
                Either.catch { okHttpClient.newCall(request).execute() }
                    .mapLeft { throwable -> PublishExternalQuestionnaireFailure.RequestNotExecuted(throwable) }
                    .flatMap { response ->
                        response.use { it: Response ->
                            when (it.code) {
                                204 -> Unit.right()
                                else -> PublishExternalQuestionnaireFailure.ErrorResponse(it.code, it.body?.string()).left()
                            }
                        }
                    }
            }
    }

    private fun builderWithUrl(formId: String): Either<PublishExternalQuestionnaireFailure, Request.Builder> =
        Either.catch { Request.Builder().url("${typeformBaseUrl}/forms/${formId}") }
            .mapLeft { PublishExternalQuestionnaireFailure.InvalidUrl }

    private fun requestBody(): String = """
        [
          {
            "op": "replace",
            "path": "/settings/is_public",
            "value": true
          }
        ]
    """.trimIndent()
}
