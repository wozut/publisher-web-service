package tcla.contexts.communications.core.application.sendpredefinedemail

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.google.gson.JsonParser
import jakarta.inject.Named
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value

@Named
class SendPredefinedEmailCommandHandler(
    @Value("\${mailjet.base-url}") private val mailjetBaseUrl: String,
    @Value("\${mailjet.api-key}") private val mailjetApiKey: String,
    @Value("\${mailjet.secret-key}") private val mailjetSecretKey: String,
    @Value("\${mailjet.sandbox-mode}") private val mailjetSandboxMode: Boolean,
    private val buildSendPredefinedEmailRequestBody: BuildSendPredefinedEmailRequestBody,
    private val okHttpClient: OkHttpClient,
) {
    fun execute(command: SendPredefinedEmailCommand) : Either<SendPredefinedEmailFailure, Unit> {
        val body = buildSendPredefinedEmailRequestBody.build(
            fromEmail = command.fromEmail,
            fromName = command.fromName,
            templateId = command.templateId,
            recipients = command.recipients,
            variables = command.variables,
            sandboxMode = mailjetSandboxMode
        )
        val authorizationHeaderValue = Credentials.basic(
            username = mailjetApiKey,
            password = mailjetSecretKey,
            charset = Charsets.UTF_8
        )
        return builderWithUrl()
            .flatMap { builder ->
                builder
                    .post(body.toRequestBody("application/json".toMediaType()))
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", authorizationHeaderValue)
                    .build().right()
            }.flatMap { request: Request ->
                Either.catch { okHttpClient.newCall(request).execute() }
                    .mapLeft { throwable -> SendPredefinedEmailFailure.RequestNotExecuted(throwable) }
                    .flatMap { response ->
                        response.use { it: Response ->
                            when (it.code) {
                                200 -> Either.catch {
                                    JsonParser.parseString(it.body!!.string())
                                        .asJsonObject["Messages"]
                                        .asJsonArray[0]
                                        .asJsonObject["Status"]
                                        .asString
                                }.mapLeft { SendPredefinedEmailFailure.UnexpectedResponse }
                                    .flatMap { status ->
                                        when (status) {
                                            "success" -> Unit.right()
                                            else -> SendPredefinedEmailFailure.ErrorResponse(
                                                it.code,
                                                it.body?.string()
                                            ).left()
                                        }
                                    }

                                else -> SendPredefinedEmailFailure.ErrorResponse(it.code, it.body?.string()).left()
                            }
                        }
                    }
            }
    }

    private fun builderWithUrl(): Either<SendPredefinedEmailFailure, Request.Builder> =
        Either.catch { Request.Builder().url("${mailjetBaseUrl}/send") }
            .mapLeft { SendPredefinedEmailFailure.InvalidUrl }
}
