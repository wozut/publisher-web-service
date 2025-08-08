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
import tcla.contexts.tcla.core.domain.externalquestionnaire.CreateExternalQuestionnaire
import tcla.contexts.tcla.core.application.failures.CreateExternalQuestionnaireFailure
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire

@Named
class TypeformCreateExternalQuestionnaire(
    @Value("\${typeform.base-url}") private val typeformBaseUrl: String,
    @Value("\${typeform.access-token}") private val typeformAccessToken: String,
    @Value("\${typeform.workspace}") private val typeformWorkspace: String,
    @Value("\${typeform.show-typeform-branding}") private val showTypeformBranding: Boolean,
    private val buildCreateExternalQuestionnaireRequestBody: BuildCreateExternalQuestionnaireRequestBody,
    private val okHttpClient: OkHttpClient
) : CreateExternalQuestionnaire {

    override fun execute(
        questionnaire: Questionnaire,
        publish: Boolean
    ): Either<CreateExternalQuestionnaireFailure, ExternalQuestionnaireId> {
        val body = buildCreateExternalQuestionnaireRequestBody.build(typeformWorkspace, publish, questionnaire, showTypeformBranding)
        return builderWithUrl()
            .flatMap { builder ->
                builder
                    .post(body.toRequestBody("application/json".toMediaType()))
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer $typeformAccessToken")
                    .build().right()
            }.flatMap { request: Request ->
                Either.catch { okHttpClient.newCall(request).execute() }
                    .mapLeft { throwable -> CreateExternalQuestionnaireFailure.RequestNotExecuted(throwable) }
                    .flatMap { response ->
                        response.use { it: Response ->
                            when (it.code) {
                                201 -> when (val location = it.header("location")) {
                                    null -> CreateExternalQuestionnaireFailure.LocationHeaderNotPresent.left()
                                    else -> ExternalQuestionnaireId(location.extractId()).right()
                                }

                                else -> CreateExternalQuestionnaireFailure.ErrorResponse(it.code, it.body?.string())
                                    .left()
                            }
                        }
                    }
            }
    }

    private fun String.extractId(): String = split("/").last()

    private fun builderWithUrl(): Either<CreateExternalQuestionnaireFailure, Request.Builder> =
        Either.catch { Request.Builder().url("${typeformBaseUrl}/forms") }
            .mapLeft { CreateExternalQuestionnaireFailure.InvalidUrl }

}
