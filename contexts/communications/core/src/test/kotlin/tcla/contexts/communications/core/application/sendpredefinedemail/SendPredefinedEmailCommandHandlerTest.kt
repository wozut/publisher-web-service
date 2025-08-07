package tcla.contexts.communications.core.application.sendpredefinedemail

import arrow.core.Either
import io.mockk.every
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.connection.RealCall
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Hashtable

class SendPredefinedEmailCommandHandlerTest {
    @Test
    fun `it calls to mailjet and it handles successful response`() {
        val okHttpClient = mockk<OkHttpClient>()
        val call = mockk<RealCall>()
        val sendPredefinedEmailCommandHandler = SendPredefinedEmailCommandHandler(
            mailjetApiKey = "whatever",
            mailjetSecretKey = "whatever",
            mailjetBaseUrl = "https://api.mailjet.com/v3.1",
            buildSendPredefinedEmailRequestBody = BuildSendPredefinedEmailRequestBody(),
            okHttpClient = okHttpClient,
            mailjetSandboxMode = false
        )
        val teamOwnerEmail = "teammember@mail.com"

        every { okHttpClient.newCall(any()) } returns call

        val expectedJson = """
            {
                "Messages": [
                    {
                        "Status": "success",
                        "CustomID": "",
                        "To": [
                            {
                                "Email": "$teamOwnerEmail",
                                "MessageUUID": "b287c020-87fd-4452-8455-ce15508acb3d",
                                "MessageID": 288230396282731595,
                                "MessageHref": "https://api.mailjet.com/v3/REST/message/288230396282731595"
                            }
                        ],
                        "Cc": [],
                        "Bcc": []
                    }
                ]
            }
        """.trimIndent()

        val expectedResponse = Response.Builder()
            .message("")
            .protocol(Protocol.HTTP_2)
            .request(Request.Builder().url("https://api.mailjet.com/v3.1/send").build())
            .code(200)
            .body(expectedJson.toResponseBody(contentType = "application/json; charset=UTF-8".toMediaType()))
            .build()

        every { call.execute() } returns expectedResponse

        val result: Either<SendPredefinedEmailFailure, Unit> = sendPredefinedEmailCommandHandler.execute(
            SendPredefinedEmailCommand(
                recipients = listOf(Recipient("Lisa", teamOwnerEmail)),
                fromName = "TCLA",
                fromEmail = "tcla@mail.com",
                templateId = "5676728",
                variables = Hashtable(hashMapOf(Pair("myVariable", "myValue")))
            )
        )

        result.fold({ Assertions.fail(it.toString()) }, { assertThat(it).isEqualTo(Unit) })
    }
}
